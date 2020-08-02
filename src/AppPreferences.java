import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.*;

public class AppPreferences {

    Gson gson = new Gson();
    String filePath = "/resources/deposit.json";

    public void storeUserData(User user) {
        try {
            FileWriter writer = new FileWriter(new File(filePath));
            writer.write(gson.toJson(user));
            writer.close();
            System.out.println("printed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEMail(){
        User json;
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        json = new Gson().fromJson(reader, User.class);
        return (json.EMail);
    }

    public String getPassword(){
        User json;
        Encrypted encrypted;
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        json = new Gson().fromJson(reader, User.class);
        encrypted = json.encrypted;
        return (encrypted.password);
    }

    public boolean testFile(){
        File file = new File(filePath);
        return file.length()>2;
    }

    public static class User {
        private String EMail;
        private String name;
        private String forename;
        private Encrypted encrypted;

        public User(String EMail, String name, String forename, Encrypted encrypted) {
            this.EMail = EMail;
            this.name = name;
            this.forename = forename;
            this.encrypted = encrypted;
        }

    }

    public static class Encrypted {
        private String password;
        private String salt;

        public Encrypted(String password, String salt) {
            this.password = password;
            this.salt = salt;
        }
    }
}
