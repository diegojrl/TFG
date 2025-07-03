package db;

import configuration.Configuration;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String H2_PATH = "h2_db/database";
    private static Database Instance;

    private final Connection db;

    private Database(Path folder) throws SQLException {
        final String dbUrl = "jdbc:h2:" + folder.resolve(H2_PATH).toAbsolutePath();
        db = DriverManager.getConnection(dbUrl);
        final String createUserTable = "CREATE TABLE IF NOT EXISTS USERS (ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, USERNAME VARCHAR2(255) UNIQUE NOT NULL)";
        final String createDeviceTable = "CREATE TABLE IF NOT EXISTS DEVICES (USERID INT REFERENCES USERS(ID), CLIENTID VARCHAR2(255) UNIQUE NOT NULL, LATENCY_SUM BIGINT, LATENCY_CNT BIGINT, MESSAGES_FAILED BIGINT, MESSAGES_CNT BIGINT, LAST_REPUTATION FLOAT4, LAST_TRUST FLOAT4 NOT NULL, PRIMARY KEY(USERID, CLIENTID))";
        final String createOpinionTable = "CREATE TABLE IF NOT EXISTS OPINIONS (SOURCEID VARCHAR2(255) REFERENCES DEVICES(CLIENTID), TARGETID VARCHAR2(255) REFERENCES DEVICES(CLIENTID), OPINION FLOAT4 NOT NULL, PRIMARY KEY(SOURCEID, TARGETID))";
        final String createDeviceAuditTable = "CREATE TABLE IF NOT EXISTS DEVICES_AUDIT (TIMESTAMP TIMESTAMP,USERID INT REFERENCES USERS(ID), CLIENTID VARCHAR2(255) NOT NULL, LATENCY_SUM BIGINT, LATENCY_CNT BIGINT, MESSAGES_FAILED BIGINT, MESSAGES_CNT BIGINT, LAST_REPUTATION FLOAT4, LAST_TRUST FLOAT4 NOT NULL, PRIMARY KEY(CLIENTID, TIMESTAMP))";
        final String createAuditTrigger = "CREATE TRIGGER IF NOT EXISTS DEVICE_AUDIT_TGR AFTER INSERT, UPDATE ON DEVICES FOR EACH ROW CALL \"db.AuditTrigger\"";
        try (Statement stm = db.createStatement()) {
            stm.addBatch(createUserTable);
            stm.addBatch(createDeviceTable);
            stm.addBatch(createOpinionTable);
            stm.addBatch(createDeviceAuditTable);
            stm.addBatch(createAuditTrigger);
            stm.executeBatch();
            db.setAutoCommit(true);
        }
    }

    public static void init(Path folder) throws SQLException {
        if (Instance == null) {
            Instance = new Database(folder);
        }
    }

    /**
     * Inserts the user if it doesn't exist
     *
     * @return userId
     */
    public static int insertUser(final String username) throws SQLException {
        Integer userId = getUserID(username);
        if (userId == null) {
            try (PreparedStatement stm = Instance.db.prepareStatement("INSERT INTO USERS (USERNAME) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                stm.setString(1, username);
                stm.executeUpdate();
                ResultSet id = stm.getGeneratedKeys();
                if (id.next()) {
                    userId = id.getInt(1);
                } else {
                    throw new SQLException("Failed to insert user '" + username + "' into database");
                }
            }
        }
        return userId;
    }

    private static Integer getUserID(final String username) throws SQLException {
        try (PreparedStatement stm = Instance.db.prepareStatement("SELECT ID FROM USERS WHERE USERNAME = ?")) {
            stm.setString(1, username);
            stm.setMaxRows(1);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        }
    }

    public static void insertDevice(Device device) throws SQLException {
        if (device.userId == null) throw new SQLException("PK userId cannot be NULL, clientId: " + device.clientId);

        try (PreparedStatement stm = Instance.db.prepareStatement("INSERT INTO DEVICES (USERID, CLIENTID, LATENCY_SUM, LATENCY_CNT, MESSAGES_FAILED, MESSAGES_CNT, LAST_REPUTATION, LAST_TRUST) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stm.setInt(1, device.userId);
            stm.setString(2, device.clientId);
            stm.setLong(3, device.latencySum);
            stm.setLong(4, device.latencyCnt);
            stm.setLong(5, device.messageFailed);
            stm.setLong(6, device.messageCnt);
            stm.setFloat(7, device.reputation);
            stm.setFloat(8, device.trust);
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Failed to insert device " + device.userId + " into database");
            }
        }
    }

    public static void updateDevice(Device device) throws SQLException {
        try (PreparedStatement stm = Instance.db.prepareStatement("UPDATE DEVICES SET LATENCY_SUM = ?, LATENCY_CNT = ?, MESSAGES_FAILED = ?, MESSAGES_CNT = ?, LAST_REPUTATION = ?, LAST_TRUST = ? WHERE CLIENTID = ?")) {
            stm.setLong(1, device.latencySum);
            stm.setLong(2, device.latencyCnt);
            stm.setLong(3, device.messageFailed);
            stm.setLong(4, device.messageCnt);
            stm.setFloat(5, device.reputation);
            stm.setFloat(6, device.trust);
            stm.setString(7, device.clientId);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new SQLException("Failed to update device " + device.clientId);
            }
        }
    }

    public static void changeDeviceOwner(final String clientId, int userId) throws SQLException {
        try (PreparedStatement stm = Instance.db.prepareStatement("UPDATE DEVICES SET USERID = ? WHERE CLIENTID = ?")) {
            stm.setInt(1, userId);
            stm.setString(2, clientId);
            if (stm.executeUpdate() != 1) {
                throw new SQLException("Failed to update device " + clientId + " in database");
            }
        }
    }

    public static Device getDevice(final String clientId) throws SQLException {
        try (PreparedStatement stm = Instance.db.prepareStatement("SELECT USERID, LATENCY_SUM, LATENCY_CNT, MESSAGES_FAILED, MESSAGES_CNT, LAST_REPUTATION, LAST_TRUST FROM DEVICES WHERE CLIENTID = ?")) {
            stm.setString(1, clientId);
            stm.setMaxRows(1);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                long latencySum = rs.getLong(2), latencyCnt = rs.getLong(3), messagesFailed = rs.getLong(4), messagesCnt = rs.getLong(5);
                float reputation = rs.getFloat(6), trust = rs.getFloat(7);
                if (latencyCnt == 0) {
                    latencyCnt = 1;
                    latencySum = Configuration.getDelayMax();
                }
                if (messagesCnt == 0) {
                    messagesCnt = 1;
                    messagesFailed = 2;
                }

                return new Device(rs.getInt(1), clientId, latencySum, latencyCnt, messagesFailed, messagesCnt, reputation, trust);
            }
            return null;
        }
    }

    public static boolean insertOpinion(String sourceId, String targetId, float opinion) throws SQLException {
        try (PreparedStatement stm = Instance.db.prepareStatement("SELECT OPINION FROM OPINIONS WHERE SOURCEID = ? AND TARGETID = ?")) {
            stm.setString(1, sourceId);
            stm.setString(2, targetId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                float oldOpinion = rs.getFloat(1);
                if (opinion == oldOpinion) {
                    return false;
                } else {
                    try (PreparedStatement stm1 = Instance.db.prepareStatement("UPDATE OPINIONS SET OPINION = ? WHERE SOURCEID = ? AND TARGETID = ?")) {
                        stm1.setFloat(1, opinion);
                        stm1.setString(2, sourceId);
                        stm1.setString(3, targetId);
                        return stm1.executeUpdate() == 1;
                    }

                }
            } else {
                try (PreparedStatement stm2 = Instance.db.prepareStatement("INSERT INTO OPINIONS(SOURCEID, TARGETID, OPINION) VALUES (?, ?, ?)")) {
                    stm2.setString(1, sourceId);
                    stm2.setString(2, targetId);
                    stm2.setFloat(3, opinion);
                    return stm2.executeUpdate() == 1;
                }
            }
        }
    }

    public static void deleteAllOpinions(String clientId) throws SQLException {
        try (PreparedStatement stm = Instance.db.prepareStatement("DELETE FROM OPINIONS WHERE TARGETID = ?")) {
            stm.setString(1, clientId);
            stm.executeUpdate();
        }
    }

    public static List<Opinion> getOpinions(final String clientId) throws SQLException {
        try (PreparedStatement stm = Instance.db.prepareStatement("SELECT O.SOURCEID, O.OPINION, D.LAST_TRUST FROM OPINIONS O JOIN DEVICES D on o.SOURCEID = D.CLIENTID WHERE O.TARGETID = ?")) {
            stm.setString(1, clientId);
            ResultSet rs = stm.executeQuery();
            List<Opinion> opinions = new ArrayList<>();
            while (rs.next()) {
                opinions.add(new Opinion(rs.getString(1), rs.getFloat(2), rs.getFloat(3)));
            }
            return opinions;
        }
    }

}
