import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import static java.awt.Font.*;
import static java.awt.Font.MONOSPACED;

public class Main {
    DataHandler handler = new DataHandler();
    AppPreferences jsonCompiler = new AppPreferences();
    int id;

    // layout assets
    GridBagConstraints c;
    CardLayout cl;

    // fonts
    Font f;

    // welcomeScreen assets
    JPanel welcome;
    JButton welcomeRegisterButton;
    JButton welcomeLoginButton;

    // register assets
    JPanel registerPanel;
    JButton registerButton;
    JTextField registerNameTextField;
    JTextField registerForenameTextField;
    JTextField registerEMailTextField;
    JPasswordField registerPasswordField;
    JButton registerCancelButton;

    static AppPreferences deposit = new AppPreferences();
    AppPreferences.User user;
    AppPreferences.Encrypted encrypted;
    PasswordEncryptionService security = new PasswordEncryptionService();

    // register data assets
    String registerForename;
    String registerName;
    String registerEMail;

    // main assets
    JPanel mainPanel;
    JPanel menuPanel;
    JButton menuBooks;
    JButton menuImport;
    JTextField searchTextField = new JTextField("", 100);
    JButton searchButton = new JButton("search");

    JTable table;
    JScrollPane scrollPane;
    String[] columnNames = {
            "ISBN",
            "Title",
            "Author",
            "Releasedate",
            "Pagecount"
    };

    // login assets
    JPanel login;
    JPanel loginField;
    JPanel loginFieldButtons;
    JButton loginButton = new JButton("Register");
    JTextField loginNameTextField = new JTextField("Surname");
    JTextField loginEMailTextField = new JTextField("E-Mail");
    JPasswordField loginPasswordField = new JPasswordField("");
    JButton loginCancelButton = new JButton("Cancel");

    String password;
    String eMail;
    String forename;
    String name;

    // import assets
    JPanel importPanel;
    JTextField importTextField = new JTextField("ISBN");
    JButton importButton = new JButton("Import");
    JButton importCancelButton = new JButton("Cancel");

    private JPanel welcomeScreen(Container pane) {
        welcome = new JPanel();
        welcome.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        welcomeRegisterButton = new JButton("register");
        welcomeRegisterButton.addActionListener(e -> {
            cl = (CardLayout) pane.getLayout();
            cl.show(pane, "REGISTER");
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        welcome.add(welcomeRegisterButton, c);
        welcomeLoginButton = new JButton("Login");
        welcomeLoginButton.addActionListener(e -> {
            cl = (CardLayout) pane.getLayout();
            cl.show(pane, "LOGIN");
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        welcome.add(welcomeLoginButton, c);
        return welcome;
    }

    private JPanel registerPanel(Container pane){
        registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        f = new Font("Courier", Font.PLAIN, 30);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.25;
        c.ipady = 40;
        registerForenameTextField = new JTextField("Forename");
        registerForenameTextField.setSize(registerPanel.getWidth(),14);
        registerForenameTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(registerForenameTextField.getText().equals("Forename")){
                    registerForenameTextField.setText("");
                }
            }
        });
        registerForenameTextField.setFont(f);
        registerPanel.add(registerForenameTextField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 0.25;
        c.ipady = 40;
        registerNameTextField = new JTextField("Surname");
        registerNameTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(registerNameTextField.getText().equals("Surname")){
                    registerNameTextField.setText("");
                }
            }
        });
        registerNameTextField.setFont(f);
        registerPanel.add(registerNameTextField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.5;
        c.weighty = 0.25;
        c.ipady = 40;
        registerEMailTextField = new JTextField("E-Mail");
        registerEMailTextField.setSize(registerPanel.getWidth(),14);
        registerEMailTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(registerEMailTextField.getText().equals("E-Mail")){
                    registerEMailTextField.setText("");
                }
            }
        });
        registerEMailTextField.setFont(f);
        registerPanel.add(registerEMailTextField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.5;
        c.weighty = 0.25;
        c.ipady = 40;
        registerPasswordField = new JPasswordField("");
        registerPasswordField.setFont(f);
        registerPanel.add(registerPasswordField, c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.25;
        c.weighty = 0.3;
        c.ipady = 20;
        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            try {
                byte[] salt = security.generateSalt();
                registerEMail = registerEMailTextField.getText();
                registerName = registerNameTextField.getText();
                registerForename = registerForenameTextField.getText();
                if(handler.checkUserAvailability(registerEMail, registerName)){
                    String registerPassword = security.base64EncodeString(security.getEncryptedPassword(registerPasswordField.getPassword(), salt));
                    encrypted = new AppPreferences.Encrypted(registerPassword, security.base64EncodeString(salt));
                    handler.insertUserData(registerName, registerForename, registerEMail, registerPassword, security.base64EncodeString(salt));
                    user = new AppPreferences.User(registerEMail, registerName, registerForename, encrypted);
                    deposit.storeUserData(user);
                    System.out.println("user data stored");
                    // switch cards
                    cl = (CardLayout) pane.getLayout();
                    cl.show(pane, "MAIN");
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
                e1.printStackTrace();
            }

        });
        registerPanel.add(registerButton, c);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.25;
        c.weighty = 0.3;
        c.ipady = 20;
        registerCancelButton = new JButton("Cancel");
        registerCancelButton.addActionListener(e -> {
            registerForenameTextField.setText("Forename");
            registerNameTextField.setText("Surname");
            registerPasswordField.setText("");
            cl = (CardLayout) pane.getLayout();
            cl.show(pane, "WELCOME");
        });
        registerPanel.add(registerCancelButton, c);
        return registerPanel;
    }

    private JPanel mainPanel(Container pane){
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        updateID();

        menuPanel = new JPanel();
        menuBooks = new JButton("Refresh");
        menuBooks.addActionListener(e -> updateTable(table));
        menuPanel.add(menuBooks);
        menuImport = new JButton("Import book");
        menuImport.addActionListener(e -> {
            cl = (CardLayout) pane.getLayout();
            cl.show(pane, "IMPORT");
        });
        menuPanel.add(menuImport);


        table = new JTable(new DefaultTableModel(null, columnNames));
        table.setAutoCreateRowSorter(true);
        updateTable(table);
        table.setFont(new Font(DIALOG, ITALIC, 18));

        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        searchTextField.setFont(new Font(MONOSPACED, 142, 15));
        searchTextField.setForeground(new Color(28, 103, 214));
        searchTextField.addActionListener(e -> {
        });
        searchTextField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }
            public void focusLost(FocusEvent e) {
            }
        });
        searchTextField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        searchButton.setFont(new Font( MONOSPACED, 142, 15));
        searchButton.setForeground(new Color(28, 103, 214));
        searchButton.addActionListener(e -> { });

        JPanel interactivePane = new JPanel();
        interactivePane.setLayout(new BorderLayout());
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                BoxLayout.LINE_AXIS));
        buttonPane.add(searchButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        interactivePane.add(buttonPane, BorderLayout.PAGE_END);
        interactivePane.add(searchTextField, BorderLayout.PAGE_START);
        mainPanel.add(menuPanel, BorderLayout.PAGE_START);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(interactivePane, BorderLayout.PAGE_END);
        return mainPanel;
    }

    private JPanel loginPanel(Container pane){
        login = new JPanel();
        login.setLayout(new BoxLayout(login, BoxLayout.PAGE_AXIS));
        f = new Font("Courier", Font.PLAIN, 30);

        loginField = new JPanel(new BorderLayout());
        loginNameTextField = new JTextField("Surname");
        loginNameTextField.setSize(login.getWidth(),14);
        loginNameTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(loginNameTextField.getText().equals("Surname")){
                    loginNameTextField.setText("");
                }
            }
        });
        loginNameTextField.setFont(f);
        loginField.add(loginNameTextField, BorderLayout.CENTER);

        loginEMailTextField = new JTextField("E-Mail");
        loginEMailTextField.setSize(login.getWidth(),14);
        loginEMailTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(loginEMailTextField.getText().equals("E-Mail")){
                    loginEMailTextField.setText("");
                }
            }
        });
        loginEMailTextField.setFont(f);
        loginField.add(loginEMailTextField, BorderLayout.NORTH);

        loginPasswordField = new JPasswordField("");
        loginPasswordField.setFont(f);
        loginPasswordField.setSize(login.getWidth(),14);
        loginField.add(loginPasswordField, BorderLayout.SOUTH);

        login.add(loginField);

        loginFieldButtons = new JPanel(new FlowLayout());

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String salt = handler.getSalt(loginNameTextField.getText(), loginEMailTextField.getText());
            try {
                if(salt!=null) {
                    char[] typed = loginPasswordField.getPassword();
                    password = security.base64EncodeString(security.getEncryptedPassword(typed, security.base64DecodeString(salt)));
                    eMail = loginEMailTextField.getText();
                    name = loginNameTextField.getText();
                    if (security.authenticate(typed, security.base64Decode(handler.getPassword(name, eMail)), security.base64DecodeString(handler.getSalt(name, eMail)))) {
                        forename = handler.getForename(eMail);
                        encrypted = new AppPreferences.Encrypted(password, salt);
                        user = new AppPreferences.User(eMail, name, forename, encrypted);
                        deposit.storeUserData(user);
                        updateID();
                        cl = (CardLayout) pane.getLayout();
                        cl.show(pane, "MAIN");
                    } else {
                        System.out.println("Password or eMail incorrect");
                    }
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
                e1.printStackTrace();
            }

        });
        loginFieldButtons.add(loginButton);

        loginCancelButton = new JButton("Cancel");
        loginCancelButton.addActionListener(e -> {
            cl = (CardLayout) pane.getLayout();
            cl.show(pane, "WELCOME");
        });
        loginFieldButtons.add(loginCancelButton);
        login.add(loginFieldButtons);
        return login;
    }

    private JPanel importPanel(Container pane){
        importPanel = new JPanel();
        importPanel.setLayout(new BorderLayout());
        importTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                importTextField.setText("");
            }
        });
        importPanel.add(importTextField, BorderLayout.NORTH);
        importButton.addActionListener(e -> {
            handler.importBook(importTextField.getText(), id);
            updateTable(table);
            cl = (CardLayout) pane.getLayout();
            cl.show(pane, "MAIN");
        });
        importPanel.add(importButton, BorderLayout.CENTER);
        importCancelButton.addActionListener(e -> {
            importTextField.setText("ISBN");
            updateTable(table);
            cl = (CardLayout) pane.getLayout();
            cl.show(pane, "MAIN");
        });
        importPanel.add(importCancelButton, BorderLayout.EAST);
        return importPanel;
    }

    private void addComponentsToPane(Container pane) {
        pane.setLayout(new CardLayout());
        pane.add(welcomeScreen(pane), "WELCOME");
        pane.add(registerPanel(pane), "REGISTER");
        pane.add(loginPanel(pane), "LOGIN");
        pane.add(mainPanel(pane), "MAIN");
        pane.add(importPanel(pane), "IMPORT");
    }

    private void updateTable(JTable table1){
        try {
            table1.setModel(handler.bookData(table1.getModel(), id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndShowGUI(String condition) {
        //Create and set up the window.
        JFrame frame;
        handler.run();

        frame = new JFrame("LibraryDesktop");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                frame.dispose();
                System.exit(0);
                handler.close();
            }
        });
        //Set up the content pane.

        addComponentsToPane(frame.getContentPane());
        cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), condition);

        //Size and display the window.
        Insets insets = frame.getInsets();
        frame.setSize(800 + insets.left + insets.right, 550 + insets.top + insets.bottom);
        frame.setVisible(true);
    }

    private void updateID(){
        try {
            id = handler.getId(jsonCompiler.getEMail(), jsonCompiler.getPassword());
        } catch(Exception e) {
            id = 0;
        }
    }

    private Main(String condition){
        updateID();
        createAndShowGUI(condition);
    }

    public static void main(String[] args) {
        String condition;
        if(deposit.testFile()){
            condition = "MAIN";
        } else {
            condition = "WELCOME";
        }
        new Main(condition);
    }
}