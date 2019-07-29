package org.drathveloper.passwordAutoUpdater;

import org.drathveloper.passwordAutoUpdater.database.DBManager;
import org.drathveloper.passwordAutoUpdater.mail.MailManager;
import org.drathveloper.passwordAutoUpdater.model.ApplicationDAO;

import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PasswordAutoUpdater implements Runnable {

    private static final char SEPARATOR = '\\';

    public PasswordAutoUpdater(){
    }

    @Override
    public void run() {
        File jarPath = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String executingPath = jarPath.getParentFile().getAbsolutePath() + SEPARATOR;
        try {
            MailManager mailManager = new MailManager(executingPath);
            List<ApplicationDAO> userList = mailManager.searchUsers();
            DBManager dbManager = new DBManager(executingPath);
            if(userList.size() > 0){
                boolean result = dbManager.updateList(userList);
                System.exit((result) ? 0 : -1);
            }
            System.exit(0);
        } catch (MessagingException ex) {
            System.exit(-2);
        } catch (IOException ex) {
            System.exit(-2);
        }
    }

}
