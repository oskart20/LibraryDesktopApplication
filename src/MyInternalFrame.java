import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


class SignInFrame extends JInternalFrame {
    JButton SignInButton = new JButton("Register");
    JTextField NameTextField = new JTextField("Surname");
    JTextField EMailTextField = new JTextField("E-Mail");
    JPasswordField PasswordField = new JPasswordField("");
    JButton CancelButton = new JButton("Cancel");
    JPanel field;
    JPanel fieldButtons;

    DataHandler handler = new DataHandler();
    AppPreferences deposit = new AppPreferences();
    AppPreferences.User user;
    AppPreferences.Encrypted encrypted;
    PasswordEncryptionService security = new PasswordEncryptionService();
    String password;
    String eMail;
    String forename;
    String name;


    public SignInFrame(Main main){
        SignInFrame signIn = this;
        signIn.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setSize(350, 180);
        setLocation(30, 30);
        signIn.rootPane.setLayout(new BoxLayout(rootPane,BoxLayout.PAGE_AXIS));

        field = new JPanel(new BorderLayout());
        NameTextField.setSize(signIn.rootPane.getWidth(),14);
        NameTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NameTextField.setText("");
            }
        });
        field.add(NameTextField, BorderLayout.CENTER);
        EMailTextField.setSize(signIn.rootPane.getWidth(),14);
        EMailTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EMailTextField.setText("");
            }
        });
        field.add(EMailTextField, BorderLayout.NORTH);
        PasswordField.setSize(signIn.rootPane.getWidth(),14);
        field.add(PasswordField, BorderLayout.SOUTH);
        this.rootPane.add(field);
        fieldButtons = new JPanel(new BorderLayout());
        fieldButtons.add(SignInButton, BorderLayout.WEST);
        SignInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String salt = handler.getSalt(NameTextField.getText(), EMailTextField.getText());
                try {
                    if(salt!=null) {
                        password = security.base64Encode(security.getEncryptedPassword(PasswordField.getPassword().toString(), security.base64Decode(salt)));
                        eMail = EMailTextField.getText();
                        if ((handler.checkUserLogin(eMail, password))) {
                            name = handler.getName(eMail);
                            forename = handler.getForename(eMail);
                            encrypted = new AppPreferences.Encrypted(password, salt);
                            user = new AppPreferences.User(eMail, name, forename, encrypted);
                            // todo: request id from db everytime we need it
                            deposit.storeUserData(user);
                            main.updateID();
                            signIn.setVisible(false);
                        } else {
                            System.out.println("Password or eMail incorrect");
                        }
                    }
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                }

            }
        });
        fieldButtons.add(CancelButton, BorderLayout.EAST);
        CancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EMailTextField.setText("E-Mail");
                PasswordField.setText("");
                signIn.setVisible(false);
            }
        });
        this.rootPane.add(fieldButtons);
    }
}

class ImportFrame extends JInternalFrame {
    JTextField importTextField = new JTextField("ISBN");
    JButton importButton = new JButton("Import");
    JButton cancelButton = new JButton("Cancel");

    DataHandler handler = new DataHandler();
    AppPreferences deposit = new AppPreferences();

    int id;

    public ImportFrame(){
        ImportFrame frame = this;
        setSize(350, 125);
        setLocation(30, 30);
        id = handler.getId(deposit.getEMail(), deposit.getPassword());
        this.rootPane.setLayout(new BoxLayout(rootPane,BoxLayout.PAGE_AXIS));
        importTextField.setSize(this.rootPane.getWidth(),14);
        importTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                importTextField.setText("");
            }
        });
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.importBook(importTextField.getText(), id);
                frame.setVisible(false);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importTextField.setText("ISBN");
                frame.setVisible(false);
            }
        });
        this.rootPane.add(importTextField);
        this.rootPane.add(importButton);
        this.rootPane.add(cancelButton);
    }
}