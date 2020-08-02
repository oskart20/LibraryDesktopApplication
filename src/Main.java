import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

    static AppPreferences deposit = new AppPreferences();
    AppPreferences.User user;
    AppPreferences.Encrypted encrypted;
    PasswordEncryptionService security = new PasswordEncryptionService();

    //threads
    private WelcomeScreen welcome;
    private RegisterPanel register;
    private LoginPanel login;
    private MainPanel mainPanel;
    private ImportPanel importPanel;

    class WelcomeScreen extends JPanel implements Runnable{

        private Thread thread;

        // welcomeScreen assets
        JButton welcomeRegisterButton;
        JButton welcomeLoginButton;

        public WelcomeScreen(Container pane) {
            this.setLayout(new GridBagLayout());
            c = new GridBagConstraints();
            welcomeRegisterButton = new JButton("Register");
            welcomeRegisterButton.addActionListener(e -> {
                cl = (CardLayout) pane.getLayout();
                cl.show(pane, "REGISTER");
            });
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            this.add(welcomeRegisterButton, c);
            welcomeLoginButton = new JButton("Login");
            welcomeLoginButton.addActionListener(e -> {
                cl = (CardLayout) pane.getLayout();
                cl.show(pane, "LOGIN");
            });
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 1;
            this.add(welcomeLoginButton, c);
        }

        public void stop(){
            System.out.println("welcome thread stopped");
            thread.interrupt();
        }

        @Override
        public void run() {
            if(thread == null){
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    class RegisterPanel extends JPanel implements Runnable{
        private Thread thread;

        // register assets
        JButton registerButton;
        JTextField registerNameTextField;
        JTextField registerForenameTextField;
        JTextField registerEMailTextField;
        JPasswordField registerPasswordField;
        JButton registerCancelButton;

        // register data assets
        String registerForename;
        String registerName;
        String registerEMail;

        public RegisterPanel(Container pane){
            this.setLayout(new GridBagLayout());
            c = new GridBagConstraints();
            f = new Font("Courier", Font.PLAIN, 30);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0.5;
            c.weighty = 0.25;
            c.ipady = 40;
            registerForenameTextField = new JTextField("Forename");
            registerForenameTextField.setSize(this.getWidth(),14);
            registerForenameTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if(registerForenameTextField.getText().equals("Forename")){
                        registerForenameTextField.setText("");
                    }
                }
            });
            registerForenameTextField.setFont(f);
            this.add(registerForenameTextField, c);

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
            this.add(registerNameTextField, c);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 2;
            c.weightx = 0.5;
            c.weighty = 0.25;
            c.ipady = 40;
            registerEMailTextField = new JTextField("E-Mail");
            registerEMailTextField.setSize(this.getWidth(),14);
            registerEMailTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if(registerEMailTextField.getText().equals("E-Mail")){
                        registerEMailTextField.setText("");
                    }
                }
            });
            registerEMailTextField.setFont(f);
            this.add(registerEMailTextField, c);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 3;
            c.weightx = 0.5;
            c.weighty = 0.25;
            c.ipady = 40;
            registerPasswordField = new JPasswordField("");
            registerPasswordField.setFont(f);
            this.add(registerPasswordField, c);

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
                        welcome.stop();
                        // switch cards
                        cl = (CardLayout) pane.getLayout();
                        cl.show(pane, "MAIN");
                        login.stop();
                        this.stop();
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
                    e1.printStackTrace();
                }

            });
            this.add(registerButton, c);

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
            this.add(registerCancelButton, c);
        }

        public void stop(){
            System.out.println("register thread stopped");
            thread.interrupt();
        }

        @Override
        public void run() {
            if(thread == null){
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    class MainPanel extends JPanel implements Runnable{

        private Thread thread;

        // main assets
        JPanel menuPanel;
        JButton menuBooks;
        JButton menuImport;
        JTextField searchTextField = new JTextField("", 100);
        JButton searchButton = new JButton("Search");

        JTable table;
        JScrollPane scrollPane;
        String[] columnNames = {
                "ISBN",
                "Title",
                "Author",
                "Release Date",
                "Page Count"
        };

        public MainPanel(Container pane){
            this.setLayout(new BorderLayout());
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
            this.add(menuPanel, BorderLayout.PAGE_START);
            this.add(scrollPane, BorderLayout.CENTER);
            this.add(interactivePane, BorderLayout.PAGE_END);
        }

        public void stop(){
            System.out.println("main thread stopped");
            thread.interrupt();
        }

        @Override
        public void run() {
            if(thread == null){
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    class LoginPanel extends JPanel implements Runnable{
        private Thread thread;

        // login assets
        JPanel loginField;
        JPanel loginFieldButtons;
        JButton loginButton;
        JTextField loginNameTextField;
        JTextField loginEMailTextField;
        JPasswordField loginPasswordField;
        JButton loginCancelButton;

        String password;
        String eMail;
        String forename;
        String name;

        public LoginPanel(Container pane){
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            f = new Font("Courier", Font.PLAIN, 30);

            loginField = new JPanel(new BorderLayout());
            loginNameTextField = new JTextField("Surname");
            loginNameTextField.setSize(this.getWidth(),14);
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
            loginEMailTextField.setSize(this.getWidth(),14);
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
            loginPasswordField.setSize(this.getWidth(),14);
            loginField.add(loginPasswordField, BorderLayout.SOUTH);

            this.add(loginField);

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
                            welcome.stop();
                            cl = (CardLayout) pane.getLayout();
                            cl.show(pane, "MAIN");
                            register.stop();
                            this.stop();
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
            this.add(loginFieldButtons);
        }

        public void stop(){
            System.out.println("login thread stopped");
            thread.interrupt();
        }

        @Override
        public void run() {
            if(thread == null){
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    class ImportPanel extends JPanel implements Runnable{
        private Thread thread;

        // import assets
        JTextField importTextField = new JTextField("ISBN");
        JButton importButton = new JButton("Import");
        JButton importCancelButton = new JButton("Cancel");

        public ImportPanel(Container pane){
            this.setLayout(new BorderLayout());
            importTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    importTextField.setText("");
                }
            });
            this.add(importTextField, BorderLayout.NORTH);
            importButton.addActionListener(e -> {
                handler.importBook(importTextField.getText(), id);
                updateTable(mainPanel.table);
                cl = (CardLayout) pane.getLayout();
                cl.show(pane, "MAIN");
            });
            this.add(importButton, BorderLayout.CENTER);
            importCancelButton.addActionListener(e -> {
                importTextField.setText("ISBN");
                updateTable(mainPanel.table);
                cl = (CardLayout) pane.getLayout();
                cl.show(pane, "MAIN");
            });
            this.add(importCancelButton, BorderLayout.EAST);
        }

        public void stop(){
            System.out.println("import thread stopped");
            thread.interrupt();
        }

        @Override
        public void run() {
            if(thread == null){
                thread = new Thread(this);
                thread.start();
            }
        }

    }

    private void addComponentsToPane(Container pane, String condition) {
        pane.setLayout(new CardLayout());
        if(condition.equals("WELCOME")){
            welcome = new WelcomeScreen(pane);
            welcome.run();
            pane.add(welcome, "WELCOME");
            register = new RegisterPanel(pane);
            register.run();
            pane.add(register, "REGISTER");
            login = new LoginPanel(pane);
            login.run();
            pane.add(login, "LOGIN");
        }

        mainPanel = new MainPanel(pane);
        mainPanel.run();
        pane.add(mainPanel, "MAIN");
        importPanel = new ImportPanel(pane);
        importPanel.run();
        pane.add(importPanel, "IMPORT");
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
                handler.close();
                mainPanel.stop();
                importPanel.stop();
                System.exit(0);
            }
        });
        //Set up the content pane.

        addComponentsToPane(frame.getContentPane(), condition);
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