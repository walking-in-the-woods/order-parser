package as.transactionparser.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OrderCommandLine implements ApplicationRunner {

    private final Logger LOGGER = LoggerFactory.getLogger("logger");

    public Resource csvResource;
    public Resource jsonResource;

    static Path currentDir = Paths.get(".");

    Pattern csvPattern = Pattern.compile("\\.csv");
    Pattern jsonPattern = Pattern.compile("\\.json");

    @Override
    @Order(1)
    public void run(ApplicationArguments args) {

        for (String filename : args.getNonOptionArgs()) {
            Matcher csvMatcher = csvPattern.matcher(filename);
            Matcher jsonMatcher = jsonPattern.matcher(filename);

            if (csvMatcher.find()) {
                csvResource = new FileSystemResource(currentDir + "/" + filename);
            }

            if (jsonMatcher.find()) {
                jsonResource = new FileSystemResource(currentDir + "/" + filename);
            }
        }

        if (csvResource != null && jsonResource != null) {
            LOGGER.info(currentDir + "\n" + csvResource.getFilename() + "\n" + jsonResource.getFilename());
        }
    }
}
