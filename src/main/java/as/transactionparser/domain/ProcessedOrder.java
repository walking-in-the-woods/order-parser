package as.transactionparser.domain;

public class ProcessedOrder {

    private String orderId;
    private String amount;
    private String currency;
    private String comment;
    private String filename;
    private String line;
    private String result;

    public ProcessedOrder() {
    }

    public ProcessedOrder(String orderId, String amount, String currency, String comment,
                          String filename, String line, String status) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.comment = comment;
        this.filename = filename;
        this.line = line;
        this.result = status;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + orderId +
                ", \"amount\":\"" + amount + "\"" +
                ", \"comment\":\"" + comment + "\"" +
                ", \"filename\":\"" + filename + "\"" +
                ", \"line=\":\"" + line + "\"" +
                ", \"result\":\"" + result + "\"" +
                '}';
    }
}
