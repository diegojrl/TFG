package org.example.db;

import org.example.configuration.Configuration;

import java.sql.*;

public class Database {
    private static final String H2_PATH = "/opt/hivemq/data/h2_db/database";
    private static Database Instance;

    private final Connection db;

    private Database() throws SQLException {
        final String dbUrl = "jdbc:h2:" + H2_PATH;
        db = DriverManager.getConnection(dbUrl);
        final String createUserTable = "CREATE TABLE IF NOT EXISTS USERS (ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, USERNAME VARCHAR2(255) UNIQUE NOT NULL)";
        final String createDeviceTable = "CREATE TABLE IF NOT EXISTS DEVICES (USERID INT REFERENCES USERS(ID), CLIENTID VARCHAR2(255) UNIQUE NOT NULL, LATENCY_SUM BIGINT, LATENCY_CNT BIGINT, MESSAGES_FAILED BIGINT, MESSAGES_CNT BIGINT, LAST_REPUTATION FLOAT4, LAST_TRUST FLOAT4 NOT NULL, PRIMARY KEY(USERID, CLIENTID))";
        Statement stm = db.createStatement();
        stm.addBatch(createUserTable);
        stm.addBatch(createDeviceTable);
        stm.executeBatch();
        db.setAutoCommit(true);
    }

    public static void init() throws SQLException {
        if (Instance == null) {
            Instance = new Database();
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
            PreparedStatement stm = Instance.db.prepareStatement("INSERT INTO USERS (USERNAME) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, username);
            stm.executeUpdate();
            ResultSet id = stm.getGeneratedKeys();
            if (id.next()) {
                userId = id.getInt(1);
            } else {
                throw new SQLException("Failed to insert user '" + username + "' into database");
            }
        }
        return userId;
    }

    private static Integer getUserID(final String username) throws SQLException {
        PreparedStatement stm = Instance.db.prepareStatement("SELECT ID FROM USERS WHERE USERNAME = ?");
        stm.setString(1, username);
        stm.setMaxRows(1);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return null;
    }

    public static void insertDevice(Device device) throws SQLException {
        if (device.userId == null) throw new SQLException("PK userId cannot be NULL, clientId: " + device.clientId);

        PreparedStatement stm = Instance.db.prepareStatement("INSERT INTO DEVICES (USERID, CLIENTID, LATENCY_SUM, LATENCY_CNT, MESSAGES_FAILED, MESSAGES_CNT, LAST_REPUTATION, LAST_TRUST) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
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

    public static void updateDevice(Device device) throws SQLException {
        PreparedStatement stm = Instance.db.prepareStatement("UPDATE DEVICES SET LATENCY_SUM = ?, LATENCY_CNT = ?, MESSAGES_FAILED = ?, MESSAGES_CNT = ?, LAST_REPUTATION = ?, LAST_TRUST = ? WHERE CLIENTID = ?");
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

    public static void changeDeviceOwner(final String clientId, int userId) throws SQLException {
        PreparedStatement stm = Instance.db.prepareStatement("UPDATE DEVICES SET USERID = ? WHERE CLIENTID = ?");
        stm.setInt(1, userId);
        stm.setString(2, clientId);
        if (stm.executeUpdate() != 1) {
            throw new SQLException("Failed to update device " + clientId + " in database");
        }
    }

    public static Device getDevice(final String clientId) throws SQLException {
        PreparedStatement stm = Instance.db.prepareStatement("SELECT USERID, LATENCY_SUM, LATENCY_CNT, MESSAGES_FAILED, MESSAGES_CNT, LAST_REPUTATION, LAST_TRUST FROM DEVICES WHERE CLIENTID = ?");
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
