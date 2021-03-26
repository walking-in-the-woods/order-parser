package as.transactionparser.configuration;

import as.transactionparser.domain.Order;
import as.transactionparser.domain.OrderFieldSetMapper;
import as.transactionparser.domain.OrderItemProcessor;
import as.transactionparser.domain.ProcessedOrder;
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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BeanFactory beanFactory;

    private final String csvSource = "/data/input.csv";
    private final String jsonSource = "/data/input.json";
    private final String csvRegex = "\\w+\\.csv";
    private final String jsonRegex = "\\w+\\.json";

    public static String extractFileName(String source, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        matcher.reset();

        if(matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    @Bean
    public FlatFileItemReader<Order> csvItemReader() {
        FlatFileItemReader<Order> reader = new FlatFileItemReader<>();

        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(csvSource));

        DefaultLineMapper<Order> orderLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] {"id", "amount", "currency", "comment"});

        orderLineMapper.setLineTokenizer(tokenizer);
        orderLineMapper.setFieldSetMapper(new OrderFieldSetMapper());
        orderLineMapper.afterPropertiesSet();

        reader.setLineMapper(orderLineMapper);

        return reader;
    }

    @Bean
    public JsonItemReader<Order> jsonItemReader() {
        return new JsonItemReaderBuilder<Order>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Order.class))
                .resource(new ClassPathResource(jsonSource))
                .name("jsonItemReader")
                .build();
    }

    @Bean
    public OrderItemProcessor csvItemProcessor() {
        return new OrderItemProcessor(extractFileName(csvSource, csvRegex));
    }

    @Bean
    public OrderItemProcessor jsonItemProcessor() {
        return new OrderItemProcessor(extractFileName(jsonSource, jsonRegex));
    }

    @Bean
    public ItemWriter<ProcessedOrder> orderItemWriter() {
        return items -> {
            for (ProcessedOrder item : items) {
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
                .<Order, ProcessedOrder> chunk(10)
                .reader(csvItemReader())
                .processor(new OrderItemProcessor(extractFileName(csvSource, csvRegex)))
                .writer(orderItemWriter())
                .build();
    }

    @Bean
    public Step jsonStep() throws NoSuchMethodException {
        return stepBuilderFactory.get("jsonStep")
                .<Order, ProcessedOrder> chunk(10)
                .reader(jsonItemReader())
                .processor(jsonItemProcessor())
                .writer(orderItemWriter())
                .build();
    }
}
