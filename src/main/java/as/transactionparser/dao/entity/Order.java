package as.transactionparser.dao.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "order_table")
public class Order {

    @Id
    @Column(name = "dbid", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column (name = "amount", nullable = false)
    private Double amount;

    @NotNull
    @Column (name = "currency", nullable = false)
    private String currency;

    @NotNull
    @Column (name = "comment", nullable = false)
    private String comment;

    @NotNull
    @Column (name = "filename", nullable = false)
    private String filename;

    @NotNull
    @Column (name = "line", nullable = false)
    private Long line;

    public Order() {
    }

    public Order(final Long id, final Double amount, final String currency,
                 final String comment, final String filename, final Long line) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.comment = comment;
        this.filename = filename;
        this.line = line;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getLine() {
        return line;
    }

    public void setLine(Long line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"amount\":\"" + amount + "\"" +
                ", \"currency\":\"" + currency + "\"" +
                ", \"comment\":\"" + comment + "\"" +
                ", \"filename\":\"" + filename + "\"" +
                ", \"line\":" + line +
                '}';
    }
}
