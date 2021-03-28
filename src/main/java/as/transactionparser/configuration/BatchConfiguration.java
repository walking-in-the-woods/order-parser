package as.transactionparser.configuration;

// mvn clean install
// java -jar target/order-parser-0.0.1.jar orders.csv orders.json

import as.transactionparser.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration implements CommandLineRunner {

    private final Logger LOGGER = LoggerFactory.getLogger("logger");

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    DefaultOrderResources defaultOrderResources;

    @Autowired
    OrderCommandLine orderCommandLine;

    @Value("#{orderCommandLine.csvResource ?: defaultOrderResources.csvResource}")
    private Resource csvResource;

    @Value("#{orderCommandLine.jsonResource ?: defaultOrderResources.jsonResource}")
    private Resource jsonResource;

    @Autowired
    private JobLauncher jobLauncher;

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
    public FlatFileItemReader<Order> csvItemReader() throws IOException {
        FlatFileItemReader<Order> reader = new FlatFileItemReader<>();

        reader.setLinesToSkip(1);
        reader.setResource(csvResource);

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
    public JsonItemReader<Order> jsonItemReader() throws IOException {
        return new JsonItemReaderBuilder<Order>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Order.class))
                .resource(jsonResource)
                .name("jsonItemReader")
                .build();
    }

    @Bean
    public OrderItemProcessor csvItemProcessor() throws IOException {
        return new OrderItemProcessor(extractFileName(csvResource.getFilename(), csvRegex));
    }

    @Bean
    public OrderItemProcessor jsonItemProcessor() throws IOException {
        return new OrderItemProcessor(extractFileName(jsonResource.getFilename(), jsonRegex));
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
    @Autowired
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
    public Step csvStep() throws IOException {
        return stepBuilderFactory.get("csvStep")
                .<Order, ProcessedOrder> chunk(10)
                .reader(csvItemReader())
                .processor(csvItemProcessor())
                .writer(orderItemWriter())
                .build();
    }

    @Bean
    public Step jsonStep() throws NoSuchMethodException, IOException {
        return stepBuilderFactory.get("jsonStep")
                .<Order, ProcessedOrder> chunk(10)
                .reader(jsonItemReader())
                .processor(jsonItemProcessor())
                .writer(orderItemWriter())
                .build();
    }

    @Override
    @org.springframework.core.annotation.Order(2)
    public void run(String... args) throws Exception {
        JobParametersBuilder jobParameters = new JobParametersBuilder();
        jobLauncher.run(job(), jobParameters.toJobParameters());
    }
}
