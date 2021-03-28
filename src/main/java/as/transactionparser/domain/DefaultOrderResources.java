package as.transactionparser.domain;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DefaultOrderResources {

    public Resource csvResource = new ClassPathResource("/data/sample.csv");
    public Resource jsonResource = new ClassPathResource("/data/sample.json");
}
