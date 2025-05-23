package org.example.db;

import org.example.trustData.DeviceTrustAttributes;

import java.sql.*;

public class Database {
    private static final String H2_PATH = "/opt/hivemq/data/h2_db/database.db";
    private static Database Instance;

    private final Connection db;

    private Database() throws SQLException {
        final String dbUrl = "jdbc:h2:" + H2_PATH;
        db = DriverManager.getConnection(dbUrl);
        final String createUserTable = "CREATE TABLE IF NOT EXISTS USERS " +
                "(ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, USERNAME VARCHAR2(255) UNIQUE NOT NULL)";
        final String createDeviceTable = "CREATE TABLE IF NOT EXISTS DEVICES " +
                "(USERID INT REFERENCES USERS(ID), CLIENTID VARCHAR2(255) UNIQUE NOT NULL, LATENCY_SUM BIGINT, LATENCY_CNT BIGINT," +
                "MESSAGES_FAILED BIGINT, MESSAGES_CNT BIGINT, LAST_REPUTATION FLOAT4, PRIMARY KEY(USERID, CLIENTID))";
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

    public static void insertUser(final String username) throws SQLException {
        if (!userExists(username)) {
            PreparedStatement stm = Instance.db.prepareStatement("INSERT INTO USERS (USERNAME) VALUES (?)");
            stm.setString(1, username);
            stm.executeUpdate();
        }
    }

    public static boolean userExists(final String username) throws SQLException {
        return getUserID(username) != null;
    }

    public static Integer getUserID(final String username) throws SQLException {
        PreparedStatement stm = Instance.db.prepareStatement("SELECT ID FROM USERS WHERE USERNAME = ?");
        stm.setString(1, username);
        stm.setMaxRows(1);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return null;
    }

    public static void insertDevice(int userId, DeviceTrustAttributes device) {
        final String query = "MERGE INTO...";
    }

    public static Device getDevice(final String clientId) throws SQLException {
        PreparedStatement stm = Instance.db.prepareStatement("SELECT USERID, LATENCY, FAILURE_RATE, LAST_REPUTATION FROM DEVICES WHERE CLIENTID = ?");
        stm.setString(1, clientId);
        stm.setMaxRows(1);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            return new Device(rs.getInt(1), clientId, rs.getInt(2), rs.getInt(3), rs.getFloat(4));
        }
        return null;

    }

}
