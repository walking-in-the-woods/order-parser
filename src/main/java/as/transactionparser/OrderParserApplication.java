package as.transactionparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OrderParserApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(OrderParserApplication.class, args);
	}
}
