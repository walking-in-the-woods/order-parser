package as.transactionparser.domain;

public class Order {

    private String orderId;
    private String amount;
    private String currency;
    private String comment;

    public Order() {
    }

    public Order(String orderId, String amount, String currency, String comment) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.comment = comment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
