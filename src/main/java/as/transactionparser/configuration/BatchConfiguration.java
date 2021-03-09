package as.transactionparser.configuration;

import as.transactionparser.components.OrderNotificationListener;
import as.transactionparser.components.OrderProcessor;
import as.transactionparser.dao.entity.Order;
import as.transactionparser.services.CsvReaderConfig;
import as.transactionparser.services.JsonReaderConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Import({CsvReaderConfig.class, JsonReaderConfig.class})
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    private CsvReaderConfig csvReaderConfig = new CsvReaderConfig();
    private JsonReaderConfig jsonReaderConfig = new JsonReaderConfig();

    @Bean
    public OrderProcessor processor() {
        return new OrderProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Order> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Order>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO order_table (id, amount, currency, comment, filename, line)" +
                        "VALUES (:id, :amount, :currency, :comment, :filename, :line)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job parallelStepsJob(OrderNotificationListener listener) {

        Flow masterFlow = new FlowBuilder<Flow>("masterFlow").start(taskletStep("step1")).build();

        Flow csvFlow = (Flow) new FlowBuilder("csvFlow").start(taskletStep("csvStep")).build();
        Flow jsonFlow = (Flow) new FlowBuilder("jsonFlow").start(taskletStep("jsonStep")).build();

        Flow slaveFlow = new FlowBuilder<Flow>("splitflow")
                .split(new SimpleAsyncTaskExecutor()).add(csvFlow, jsonFlow).build();

        return (jobBuilderFactory.get("parallelFlowJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(masterFlow)
                .next(slaveFlow)
                .build()).build();

    }

    private TaskletStep taskletStep(String step) {
        return stepBuilderFactory.get(step).tasklet((contribution, chunkContext) -> {
            return RepeatStatus.FINISHED;
        }).build();

    }

    @Bean
    public Step csvStep(JdbcBatchItemWriter<Order> writer) {
        return stepBuilderFactory.get("csvStep")
                .<Order, Order> chunk(10)
                .reader(csvReaderConfig.csvItemReader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public Step jsonStep(JdbcBatchItemWriter<Order> writer) throws NoSuchMethodException {
        return stepBuilderFactory.get("jsonStep")
                .<Order, Order> chunk(10)
                .reader(jsonReaderConfig.jsonItemReader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
