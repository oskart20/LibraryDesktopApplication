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
    JTextField t1 = new JTextField("", 100);
    JButton b2 = new JButton("search");
    JButton b3 = new JButton("filter");

    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    JTable table;
    JScrollPane scrollPane;
    String[] columnNames = {
            "ISBN",
            "Title",
            "Author",
            "Releasedate",
            "Pagecount"};

    DataHandler handler = new DataHandler();
    RegisterFrame registerFrame;
    ImportFrame importFrame;
    SignInFrame signInFrame;
    AppPreferences jsonCompiler = new AppPreferences();
    int id;


    public void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());
        updateID();
        importFrame = new ImportFrame();
        pane.add(importFrame);
        signInFrame = new SignInFrame(this);
        pane.add(signInFrame);

        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        menuItem = new JMenuItem("Books");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable(table);
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Import book");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importFrame.setVisible(true);
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Sign in");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signInFrame.setVisible(true);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);

        table = new JTable(new DefaultTableModel(null, columnNames));
        table.setAutoCreateRowSorter(true);
        updateTable(table);
        table.setFont(new Font(DIALOG , ITALIC, 18));

        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        t1.setFont(new Font( MONOSPACED, 142, 15));
        t1.setForeground(new Color(28, 103, 214));
        t1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        t1.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                }
                public void focusLost(FocusEvent e) {
                }
            });
        t1.addMouseListener(new MouseListener() {
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
            b2.setFont(new Font( MONOSPACED, 142, 15));
            b2.setForeground(new Color(28, 103, 214));
            b2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            b3.setFont(new Font( MONOSPACED, 142, 15));
            b3.setForeground(new Color(28, 103, 214));
            b3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });

            JPanel interactivePane = new JPanel();
            interactivePane.setLayout(new BorderLayout());
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane,
                    BoxLayout.LINE_AXIS));
            buttonPane.add(b2);
            buttonPane.add(b3);
            buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            interactivePane.add(buttonPane, BorderLayout.PAGE_END);
            interactivePane.add(t1, BorderLayout.PAGE_START);
            pane.add(scrollPane, BorderLayout.PAGE_START);
            pane.add(interactivePane, BorderLayout.CENTER);
        }

        public void updateTable(JTable table1){
            try {
                table1.setModel(handler.bookData(table1.getModel(), id));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void createAndShowGUI() {
            //Create and set up the window.
            JFrame frame;

            frame = new JFrame("LibraryDesktop");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //Set up the content pane.
            addComponentsToPane(frame.getContentPane());

            frame.setJMenuBar(menuBar);

            //Size and display the window.
            Insets insets = frame.getInsets();
            frame.setSize(800 + insets.left + insets.right, 550 + insets.top + insets.bottom);
            frame.setVisible(true);
        }

        public void updateID(){
            id = handler.getId(jsonCompiler.getEMail(), jsonCompiler.getPassword());
        }

        public Main(){
            javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
        }


}

class RegisterFrame extends JFrame {
    RegisterFrame one;

    JButton registerButton = new JButton("Register");
    JTextField EMailTextField = new JTextField("E-Mail");
    JTextField ForenameTextField = new JTextField("Forename");
    JTextField NameTextField = new JTextField("Name");
    JPasswordField passwordField = new JPasswordField("");
    JPanel field;
    JPanel field2;
    JPanel fieldButtons;

    DataHandler handler = new DataHandler();
    static AppPreferences deposit = new AppPreferences();
    AppPreferences.User user;
    AppPreferences.Encrypted encrypted;
    PasswordEncryptionService security = new PasswordEncryptionService();
    String password;
    String eMail;
    String name;
    String forename;

    public RegisterFrame(){
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame;

        frame = new JFrame("LibraryDesktop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        //Size and display the window.
        Insets insets = frame.getInsets();
        frame.setSize(800 + insets.left + insets.right, 550 + insets.top + insets.bottom);
        frame.setVisible(true);
    }

    public void addComponentsToPane(Container pane) {
        one = this;
        setSize(350, 180);
        setLocation(30, 30);
        pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
        field = new JPanel(new BorderLayout());
        ForenameTextField.setSize(pane.getWidth(),14);
        ForenameTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ForenameTextField.setText("");
            }

        });
        field.add(ForenameTextField, BorderLayout.NORTH);
        NameTextField.setSize(pane.getWidth(),14);
        NameTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NameTextField.setText("");
            }
        });
        field.add(NameTextField, BorderLayout.SOUTH);
        field2 = new JPanel(new BorderLayout());
        EMailTextField.setSize(pane.getWidth(),14);
        EMailTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EMailTextField.setText("");
            }

        });
        field2.add(EMailTextField, BorderLayout.NORTH);
        passwordField.setSize(pane.getWidth(),14);
        field2.add(passwordField, BorderLayout.SOUTH);
        pane.add(field);
        pane.add(field2);
        fieldButtons = new JPanel(new BorderLayout());
        fieldButtons.add(registerButton, BorderLayout.WEST);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    byte[] salt = security.generateSalt();
                    eMail = EMailTextField.getText();
                    name = NameTextField.getText();
                    forename = ForenameTextField.getText();
                    if(handler.checkUserAvailability(eMail, name)){
                        password = security.base64Encode(security.getEncryptedPassword(passwordField.getPassword().toString(), salt));
                        encrypted = new AppPreferences.Encrypted(password, security.base64Encode(salt));
                        handler.insertUserData(name, forename, eMail, password, security.base64Encode(salt));
                        user = new AppPreferences.User(eMail, name, forename, encrypted);
                        deposit.storeUserData(user);
                        System.out.println("user data stored");
                    }
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                new Main();
                one.dispose();
            }
        });
        pane.add(fieldButtons);
    }

    public static void main(String[] args) {
        if(deposit.testFile()){
            new RegisterFrame();
        } else {
            new Main();
        }

    }
}