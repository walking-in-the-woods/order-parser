package as.transactionparser.domain;

import org.springframework.batch.item.ItemProcessor;

import java.util.Currency;
import java.util.Set;
import java.util.regex.Pattern;

public class OrderItemProcessor implements ItemProcessor<Order, ProcessedOrder> {

    private String filename;

    Set<Currency> currencies = Currency.getAvailableCurrencies();

    Pattern idPattern = Pattern.compile("\\d+");
    Pattern amountPattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    String[] checks = {
            "ID_ERROR",
            "AMOUNT_ERROR",
            "CURRENCY_ERROR",
    };

    int counter = 1;

    public OrderItemProcessor(String filename) {
        this.filename = filename;
    }

    @Override
    public ProcessedOrder process(Order order) throws Exception {
        ProcessedOrder processedOrder = new ProcessedOrder();

        processedOrder.setOrderId(order.getOrderId());
        processedOrder.setAmount(order.getAmount());
        processedOrder.setCurrency(order.getCurrency());
        processedOrder.setComment(order.getComment());
        processedOrder.setFilename(filename);
        processedOrder.setLine(Integer.valueOf(++counter).toString());

        if (idPattern.matcher(order.getOrderId()).matches()) {
            checks[0] = "ID_OK";
        }

        if (amountPattern.matcher(order.getAmount()).matches()) {
            checks[1] = "AMOUNT_OK";
        }

        for (Currency currency : currencies) {
            if (currency.getCurrencyCode().equalsIgnoreCase(order.getCurrency())) {
                checks[2] = "CURRENCY_OK";
            }
        }

        if (checks[0] == "ID_OK" && checks[1] == "AMOUNT_OK" && checks[2] == "CURRENCY_OK") {
            processedOrder.setResult("OK");
        } else {
            processedOrder.setResult(checks[0] + ", " + checks[1] + ", " + checks[2]);
        }

        return processedOrder;
    }
}
