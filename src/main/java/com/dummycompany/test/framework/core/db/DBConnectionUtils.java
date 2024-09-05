package com.dummycompany.test.framework.core.db;

import com.dummycompany.test.framework.core.utils.Encoding;
import org.junit.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectionUtils {

    private final Logger LOG = LogManager.getLogger(DBConnectionUtils.class);

    Connection conn;
    String url;
    String userName;
    String password;

    Statement sqlStatement;
    ResultSet resultSet;

    public DBConnectionUtils(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length==0)
            System.out.println("Path to DB Properties file needs to be passed as a command line argument");
        else {
            String url = args[0];
            String userName = args[1];
            String password = args[2];

            DBConnectionUtils dbConnectionUtils =  new DBConnectionUtils(url, userName, password);
            dbConnectionUtils.createDBConnection();
        }

    }

    public Connection createDBConnection() throws Exception {

        LOG.info("Creating Database Connection");
        LOG.info("*******************************");
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        conn = DriverManager.getConnection(url, userName, Encoding.decodePassword(password));

        if (!(conn == null) || !conn.isClosed()) {
            LOG.info("Database connection established");
        } else {
            LOG.info("Could not connect to DB");
            Assert.fail("Could not connect to DB");
        }

        return conn;
    }

    public ResultSet queryDB(String sqlQuery) throws Exception {

        if (conn == null) {
            conn = createDBConnection();
        }

        sqlStatement = conn.createStatement();
        resultSet = sqlStatement.executeQuery(sqlQuery);

        return resultSet;
    }

    public void closeDBConnection() throws Exception {

        if (conn != null && !conn.isClosed()) {
            LOG.info("***************************");
            LOG.info("Closing Database Connection");
            conn.close();
        }

    }

}
