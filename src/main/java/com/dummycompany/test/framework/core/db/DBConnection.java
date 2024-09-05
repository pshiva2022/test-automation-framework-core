package com.dummycompany.test.framework.core.db;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.dummycompany.test.framework.core.utils.Encoding;
import com.dummycompany.test.framework.core.utils.Props;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class DBConnection {

    private static final Logger LOG = LogManager.getLogger(DBConnection.class);
    static Session session = null;
    static String dbuserName = null;
    static String dbpassword = null;
    static String url = null;
    static String driverName = null;
    static Connection conn = null;
    static int lport = 0;
    static int rport = 0;
    static String dBhost = null;
    static String jumpUser = null;
    static String jumpMachine = null;
    static String jumpMachine2 = null;
    static String jumpPassword = null;

    public static void main(String[] args) throws Exception {
        if (args == null || args.length==0)
            System.out.println("Path to DB Properties file needs to be passed as a command line argument");
        else {
            getDBConnection(args[0]);
            closeDbConnection(conn);
        }
    }

    public static Connection getDBConnection(String pathToDBProperties) throws Exception {


        FileInputStream fileInputStream = new FileInputStream(pathToDBProperties);
        final Properties props = new Properties();
        props.load(fileInputStream);

        dbuserName = props.getProperty("dbuserName");
        dbpassword = props.getProperty("dbpassword");
        dBhost = props.getProperty("dBhost");
        url = props.getProperty("url");
        driverName = props.getProperty("driverName");

        lport = Integer.parseInt(props.getProperty("lport"));
        rport = Integer.parseInt(props.getProperty("rport"));

        jumpUser = props.getProperty("jumpUser");
        jumpPassword = props.getProperty("jumpPassword");
        jumpMachine = props.getProperty("jumpMachine");
        jumpMachine2 = props.getProperty("jumpMachine2");

        LOG.info("Creating a session to connect to Jump server ............");

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch jsch = new JSch();

        session = jsch.getSession(jumpUser, jumpMachine, 22);
        session.setPassword(Encoding.decodePassword(jumpPassword));
        session.setConfig(config);

        session.connect();
        LOG.info("Connected to Jump Server");

        int i = 0;

        do {
            i++;
            try {

                LOG.info("Trying to connect to DB....");
                int assignedPort = session.setPortForwardingL(lport, dBhost, 1521);
                Class.forName(driverName).newInstance();
                conn = DriverManager.getConnection(url, dbuserName, Encoding.decodePassword(dbpassword));

            } catch (Exception e) {

                LOG.info("Trying to connect to DB....");
                int assignedPort = session.setPortForwardingL(rport, dBhost, 1521);
                Class.forName(driverName).newInstance();
                conn = DriverManager.getConnection(url, dbuserName, Encoding.decodePassword(dbpassword));
            }

        } while ((conn == null || conn.isClosed()) && i <= 3);

        if (!(conn == null) || !conn.isClosed()) {
            LOG.info("Database connection established");
            LOG.info("*******************************\n");
        } else {
            assertTrue("Could not connect to EOMSYS DB", false);
        }
        fileInputStream.close();
        return conn;

    }

    public static void closeDbConnection(Connection conn) throws Exception {

        if (conn != null && !conn.isClosed()) {
            LOG.info("***************************");
            LOG.info("Closing Database Connection");
            conn.close();
        }

        if (session != null || session.isConnected()) {
            LOG.info("Closing Jump Server Connection");
            session.disconnect();
        }

    }

}
