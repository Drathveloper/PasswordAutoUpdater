package org.drathveloper.passwordAutoUpdater.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBC {

    private static final String PROPERTIES_FILE = "application.properties";

    private static Connection connection = null;

    private static String configPath = "";

    private JDBC() {
        try {
            Properties props = new Properties();
            FileInputStream propsFile = new FileInputStream(configPath + PROPERTIES_FILE);
            props.load(propsFile);
            //String driver = props.getProperty(DBConstants.DATABASE_DRIVER);
            //Class.forName(driver);
            String url = props.getProperty(DBConstants.DATABASE_URL);
            String name = props.getProperty(DBConstants.DATABASE_NAME);
            String user = props.getProperty(DBConstants.USER_PROPERTY);
            String password = props.getProperty(DBConstants.PASSWORD_PROPERTY);
            String port = props.getProperty(DBConstants.DATABASE_PORT);
            connection = DriverManager.getConnection(url + ":" + port + "/" + name, user, password);
            propsFile.close();
        } catch (SQLException ex){
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void setConfigPath(String path){
        configPath = path;
    }

    public static Connection getConnection(){
        if(connection==null){
            new JDBC();
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        connection.close();
    }

}
