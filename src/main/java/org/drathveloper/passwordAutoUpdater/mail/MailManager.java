package org.drathveloper.passwordAutoUpdater.mail;

import com.sun.mail.imap.IMAPFolder;
import org.drathveloper.passwordAutoUpdater.model.ApplicationDAO;

import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MailManager {

    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";

    private Store store;

    private String inbox;

    private String updateSubject;

    public MailManager(String propertiesPath) throws IOException, MessagingException{
        FileInputStream propsFile = new FileInputStream(propertiesPath + APPLICATION_PROPERTIES_FILE );
        Properties props = new Properties();
        props.load(propsFile);
        this.inbox = props.getProperty(MailConstants.PROPS_MAIL_INBOX);
        this.updateSubject = props.getProperty(MailConstants.PROPS_MAIL_UPDATE_SUBJECT);
        this.store = this.createStore(props);
        propsFile.close();
    }

    public List<ApplicationDAO> searchUsers() throws MessagingException, IOException {
        Folder emailFolder = store.getFolder(inbox);
        emailFolder.open(Folder.READ_WRITE);
        FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message[] messages = emailFolder.search(flagTerm);
        List<ApplicationDAO> userList = new ArrayList<>();
        for(Message message : messages){
            String subject = message.getSubject();
            if(subject.toLowerCase().contains(updateSubject)){
                String messageBody = this.getBody(message.getContent());
                List<ApplicationDAO> foundUsers = this.getFoundUsers(messageBody);
                userList.addAll(foundUsers);
            }
        }
        emailFolder.close();
        return userList;
    }

    private List<ApplicationDAO> getFoundUsers(String messageBody){
        List<ApplicationDAO> userList = new ArrayList<>();
        String[] messageLines = messageBody.split("\r");
        String user = null;
        String pass = null;
        for(String messageLine : messageLines){
            if(messageLine.toLowerCase().contains(MailConstants.FOUND_USER_LABEL) && messageLine.contains(MailConstants.LABEL_SEPARATOR)){
                user = messageLine.split(MailConstants.LABEL_SEPARATOR)[1].trim();
            } else if(messageLine.toLowerCase().contains(MailConstants.FOUND_PASS_LABEL) && messageLine.contains(MailConstants.LABEL_SEPARATOR)){
                pass = messageLine.split(MailConstants.LABEL_SEPARATOR)[1].trim();
            }
            if(user!=null && pass!=null){
                ApplicationDAO application = new ApplicationDAO();
                application.setUsername(user);
                application.setPassword(pass);
                userList.add(application);
                user = null;
                pass = null;
            }
        }
        return userList;
    }

    private String getBody(Object messageContent) throws MessagingException, IOException {
        if(messageContent!=null){
            if(messageContent instanceof MimeMultipart){
                MimeMultipart content = (MimeMultipart) messageContent;
                return this.getStringFromParts(content);
            } else if(messageContent instanceof String) {
                return (String) messageContent;
            }
        }
        return null;
    }

    private String getStringFromParts(MimeMultipart content) throws IOException, MessagingException {
        for(int i=0; i<content.getCount(); i++){
            BodyPart part = content.getBodyPart(i);
            if(part.isMimeType(MailConstants.HTML_MIME) && part.isMimeType(MailConstants.TEXT_MIME)){
                return (String) part.getContent();
            }
            String out = this.getBody(part.getContent());
            if(out!=null){
                return out;
            }
        }
        return null;
    }

    private Store createStore(Properties applicationProperties) throws MessagingException {
        Properties mailProperties = this.generateMailProperties();
        Session emailSession = Session.getInstance(mailProperties);
        Store store = emailSession.getStore(applicationProperties.getProperty(MailConstants.PROPS_MAIL_PROTOCOL));
        String host = applicationProperties.getProperty(MailConstants.PROPS_MAIL_HOST);
        String user = applicationProperties.getProperty(MailConstants.PROPS_MAIL_USER);
        String password = applicationProperties.getProperty(MailConstants.PROPS_MAIL_PASSWORD);
        store.connect(host, user, password);
        return store;
    }

    private Properties generateMailProperties(){
        Properties props = new Properties();
        props.setProperty(MailConstants.PROPS_IMAP_SSL, "true");
        return props;
    }
}
