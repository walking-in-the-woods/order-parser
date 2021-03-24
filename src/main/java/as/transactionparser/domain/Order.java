package as.transactionparser.domain;

public class Order {

    private Long orderId;
    private Double amount;
    private String currency;
    private String comment;

    public Order() {
    }

    public Order(final Long id, final Double amount, final String currency, final String comment) {
        this.orderId = id;
        this.amount = amount;
        this.currency = currency;
        this.comment = comment;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
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

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + orderId +
                ", \"amount\":\"" + amount + "\"" +
                ", \"currency\":\"" + currency + "\"" +
                ", \"comment\":\"" + comment + "\"" +
                '}';
    }
}
