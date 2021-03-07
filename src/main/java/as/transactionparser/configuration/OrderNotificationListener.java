package as.transactionparser.configuration;

import as.transactionparser.dao.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.management.NotificationListener;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);
    private List<Order> list = new ArrayList<>();

    @Autowired
    public OrderNotificationListener(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

            jdbcTemplate.query("SELECT id, amount, currency, comment, filename, line FROM order_table",
                    (rs, row) -> new Order(
                            rs.getLong(1),
                            rs.getDouble(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getLong(6))
            ).forEach(order -> list.add(order.getId().intValue()-1, order));
        }
    }

    public List<Order> getList() {
        return list;
    }
}
