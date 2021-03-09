package as.transactionparser.services;

import as.transactionparser.components.OrderFieldSetMapper;
import as.transactionparser.dao.entity.Order;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import static as.transactionparser.components.FileProcessingCommandLine.csvFile;

@Service
public class CsvReaderConfig {

    @Bean
    public FlatFileItemReader<Order> csvItemReader() {

        return new FlatFileItemReaderBuilder<Order>()
                .name("csvItemReader")
                .resource(new ClassPathResource("classpath:/" + csvFile))
                .delimited()
                .names(new String[]{"id", "amount", "currency", "comment"})
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Order>() {{
                    setTargetType(Order.class);
                }})
                .build();
    }

    @Bean
    public LineMapper<Order> lineMapper() {

        final DefaultLineMapper<Order> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[] {"id", "amount", "currency", "comment"});

        final OrderFieldSetMapper fieldSetMapper = new OrderFieldSetMapper();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }
}
