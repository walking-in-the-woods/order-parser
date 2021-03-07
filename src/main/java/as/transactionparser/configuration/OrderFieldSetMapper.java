package as.transactionparser.configuration;

import as.transactionparser.dao.entity.Order;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
public class OrderFieldSetMapper implements FieldSetMapper<Order> {

    @Override
    public Order mapFieldSet(FieldSet fieldSet) throws BindException {
        final Order order = new Order();

        order.setId(fieldSet.readLong("id"));
        order.setAmount(fieldSet.readDouble("amount"));
        order.setCurrency(fieldSet.readString("currency"));
        order.setComment(fieldSet.readString("comment"));
        order.setFilename("input.csv");
        order.setLine(1L);

        return order;
    }
}
