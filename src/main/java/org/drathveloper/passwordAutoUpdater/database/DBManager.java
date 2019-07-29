package org.drathveloper.passwordAutoUpdater.database;

import org.drathveloper.passwordAutoUpdater.model.ApplicationDAO;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DBManager {

    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";

    private String updateTable;

    public DBManager(String propsPath) throws IOException  {
        FileInputStream propsFile = new FileInputStream(propsPath + APPLICATION_PROPERTIES_FILE );
        Properties props = new Properties();
        props.load(propsFile);
        this.updateTable = props.getProperty(DBConstants.DATABASE_TABLE);
        JDBC.setConfigPath(propsPath);
        propsFile.close();
    }

    public boolean updateList(List<ApplicationDAO> userList){
        if(userList.size() > 0){
            Connection conn = JDBC.getConnection();
            try {
                for(ApplicationDAO app : userList){
                    String sql = "UPDATE " + updateTable + " SET password = ? WHERE user = ?";
                    PreparedStatement p =conn.prepareStatement(sql);
                    p.setString(1, app.getPassword());
                    p.setString(2, app.getUsername());
                    p.executeUpdate();
                }
                conn.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
}
