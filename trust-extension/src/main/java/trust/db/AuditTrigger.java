package trust.db;

import org.h2.api.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class AuditTrigger implements Trigger {
    private static final Logger log = LoggerFactory.getLogger(AuditTrigger.class);

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        try (PreparedStatement stm = connection.prepareStatement("INSERT INTO DEVICES_AUDIT (TIMESTAMP, USERID, CLIENTID, LATENCY_SUM, LATENCY_CNT, MESSAGES_FAILED, MESSAGES_CNT, LAST_REPUTATION, LAST_TRUST) VALUES (?,?,?,?,?,?,?,?,?)")) {
            log.debug("Update/Insert trigger");
            Timestamp timestamp = Timestamp.from(Instant.now());
            stm.setTimestamp(1, timestamp);
            stm.setInt(2, (Integer) newRow[0]);
            stm.setString(3, (String) newRow[1]);
            stm.setLong(4, (Long) newRow[2]);
            stm.setLong(5, (Long) newRow[3]);
            stm.setLong(6, (Long) newRow[4]);
            stm.setLong(7, (Long) newRow[5]);
            stm.setFloat(8, (Float) newRow[6]);
            stm.setFloat(9, (Float) newRow[7]);
            stm.execute();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
