package as.transactionparser.components;

import as.transactionparser.dao.entity.Order;
import org.springframework.batch.item.ItemProcessor;

public class OrderProcessor implements ItemProcessor<Order, Order> {

    @Override
    public Order process(Order order) throws Exception {
        final Long id = order.getId();
        final Double amount = order.getAmount();
        final String currency = order.getCurrency();
        final String comment = order.getComment();
        final String filename = order.getFilename();
        final Long line = order.getLine();

        Order processedOrder = new Order();
        processedOrder.setId(id);
        processedOrder.setAmount(amount);
        processedOrder.setCurrency(currency);
        processedOrder.setComment(comment);
        processedOrder.setFilename(filename);
        processedOrder.setLine(line);

        return processedOrder;
    }
}
