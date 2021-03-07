package as.transactionparser;

import as.transactionparser.configuration.OrderNotificationListener;
import as.transactionparser.dao.entity.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;

@SpringBootApplication
public class TransactionParserApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(TransactionParserApplication.class, args);

		OrderNotificationListener listener = (OrderNotificationListener) ctx.getBean(OrderNotificationListener.class);

		ArrayList<Order> orders = (ArrayList<Order>) listener.getList();

		for(Order order : orders) {
			System.out.println(order);
		}
	}
}
