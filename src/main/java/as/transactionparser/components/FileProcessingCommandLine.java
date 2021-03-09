package as.transactionparser.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FileProcessingCommandLine implements CommandLineRunner {

    @Autowired
    public static String csvFile;
    @Autowired
    public static String jsonFile;

    @Override
    public void run(String... strings) throws Exception {
        String csvRegex = "\\.csv";
        String jsonRegex = "\\.json";

        Pattern csvPattern = Pattern.compile(csvRegex);
        Pattern jsonPattern = Pattern.compile(jsonRegex);

        if(strings.length > 0) {
            for (String filename: strings) {
                Matcher csvMatcher = csvPattern.matcher(filename);
                Matcher jsonMatcher = jsonPattern.matcher(filename);

                if (csvMatcher.matches()) {
                    csvFile = filename;
                } else if (jsonMatcher.matches()) {
                    jsonFile = filename;
                }
            }
        } else {
            csvFile = "input.csv";
            jsonFile = "input.json";
        }
    }
}
