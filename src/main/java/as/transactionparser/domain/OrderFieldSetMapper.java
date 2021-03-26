package as.transactionparser.domain;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class OrderFieldSetMapper implements FieldSetMapper<Order> {

    @Override
    public Order mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Order(fieldSet.readString("id"),
                fieldSet.readString("amount"),
                fieldSet.readString("currency"),
                fieldSet.readString("comment"));
    }
}
