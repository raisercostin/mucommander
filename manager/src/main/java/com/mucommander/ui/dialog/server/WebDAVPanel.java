package com.mucommander.ui.dialog.server;

import com.mucommander.commons.file.Credentials;
import com.mucommander.commons.file.FileProtocols;
import com.mucommander.commons.file.FileURL;
import com.mucommander.text.Translator;
import com.mucommander.ui.main.MainFrame;
import java.net.MalformedURLException;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Mathias
 */
public class WebDAVPanel extends ServerPanel {

    private JTextField serverField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private static String lastServer = "";
    private static String lastUsername = System.getProperty("user.name");
    private static String lastPassword = "";

    WebDAVPanel(ServerConnectDialog dialog, final MainFrame mainFrame) {
        super(dialog, mainFrame);

        // Server field, initialized to last server entered
        serverField = new JTextField(lastServer);
        serverField.selectAll();
        addTextFieldListeners(serverField, true);
        addRow(Translator.get("server_connect_dialog.server"), serverField, 15);

        // Username field, initialized to last username
        usernameField = new JTextField(lastUsername);
        usernameField.selectAll();
        addTextFieldListeners(usernameField, false);
        addRow(Translator.get("server_connect_dialog.username"), usernameField, 15);

//        // Password field, initialized to ""
        passwordField = new JPasswordField("");
        
        passwordField.selectAll();
        addTextFieldListeners(passwordField, false);
        addRow(Translator.get("password"), passwordField, 15);

    }

    private void updateValues() {
        lastServer = serverField.getText();
        lastUsername = usernameField.getText();
        lastPassword = passwordField.getText();
    }

    ////////////////////////////////
    // ServerPanel implementation //
    ////////////////////////////////
    @Override
    FileURL getServerURL() throws MalformedURLException {
        updateValues();

        FileURL url = FileURL.getFileURL(FileProtocols.WEBDAV + "://"+lastUsername+":"+lastPassword+"@" + lastServer);

        url.setCredentials(new Credentials(lastUsername, lastPassword));

        // Set port
        url.setPort(FileURL.getRegisteredHandler(FileProtocols.WEBDAV).getStandardPort());

        return url;
    }

    @Override
    boolean usesCredentials() {
        return true;
    }

    @Override
    public void dialogValidated() {
        updateValues();
    }
}
