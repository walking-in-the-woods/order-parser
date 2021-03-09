package as.transactionparser.services;

import as.transactionparser.dao.entity.Order;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import static as.transactionparser.components.FileProcessingCommandLine.jsonFile;

@Service
public class JsonReaderConfig {

    @Bean
    public JsonItemReader<Order> jsonItemReader() {
        return new JsonItemReaderBuilder<Order>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Order.class))
                .resource(new ClassPathResource("classpath:/" + jsonFile))
                .name("jsonItemReader")
                .build();
    }
}
