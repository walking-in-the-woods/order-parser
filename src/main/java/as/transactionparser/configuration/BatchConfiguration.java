package as.transactionparser.configuration;

import as.transactionparser.domain.Order;
import as.transactionparser.domain.OrderFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Order> csvItemReader() {
        FlatFileItemReader<Order> reader = new FlatFileItemReader<>();

        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("/data/input.csv"));

        DefaultLineMapper<Order> orderLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] {"id", "amount", "currency", "comment"});

        orderLineMapper.setLineTokenizer(tokenizer);
        orderLineMapper.setFieldSetMapper(new OrderFieldSetMapper());
        orderLineMapper.afterPropertiesSet();

        reader.setLineMapper(orderLineMapper);

        return reader;
    }

    public JsonItemReader<Order> jsonItemReader() {
        return new JsonItemReaderBuilder<Order>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Order.class))
                .resource(new ClassPathResource("/data/input.json"))
                .name("jsonItemReader")
                .build();
    }

    @Bean
    public ItemWriter<Order> orderItemWriter() {
        return items -> {
            for (Order item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public Job job() throws Exception {

        Flow csvFlow = (Flow) new FlowBuilder("csvFlow").start(csvStep()).build();
        Flow jsonFlow = (Flow) new FlowBuilder("jsonFlow").start(jsonStep()).build();

        return jobBuilderFactory.get("job")
                .start(csvFlow)
                .split(new SimpleAsyncTaskExecutor())
                .add(jsonFlow)
                .end().build();

    }

    @Bean
    public Step csvStep() {
        return stepBuilderFactory.get("csvStep")
                .<Order, Order> chunk(10)
                .reader(csvItemReader())
                .writer(orderItemWriter())
                .build();
    }

    @Bean
    public Step jsonStep() throws NoSuchMethodException {
        return stepBuilderFactory.get("jsonStep")
                .<Order, Order> chunk(10)
                .reader(jsonItemReader())
                .writer(orderItemWriter())
                .build();
    }
}
