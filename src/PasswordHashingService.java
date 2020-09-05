import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

public class PasswordHashingService {

    public boolean authenticate(char[] attemptedPassword, byte[] hashedPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Hash the clear-text password using the same salt that was used to
        // encrypt the original password
        byte[] hashedAttemptedPassword = getHashedPassword(attemptedPassword, salt);

        // Authentication succeeds if hashed password that the user entered
        // is equal to the stored hash
        return Arrays.equals(hashedPassword, hashedAttemptedPassword);
    }

    public byte[] getHashedPassword(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // PBKDF2 with SHA-1 as the hashing algorithm
        String algorithm = "PBKDF2WithHmacSHA1";
        // SHA-1 generates 160 bit hashes
        int derivedKeyLength = 256;
        //iterations must at least be 1000
        int iterations = 10000;

        KeySpec spec = new PBEKeySpec(password, salt, iterations, derivedKeyLength);

        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return f.generateSecret(spec).getEncoded();
    }

    public byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        // Generate a 32 byte (256 bit) salt
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        return salt;
    }

    public char[] base64Encode(byte[] bytes){
        return DatatypeConverter.printBase64Binary(bytes).toCharArray();
    }

    public byte[] base64Decode(char[] string){
        return DatatypeConverter.parseBase64Binary(Arrays.toString(string));
    }

    public String base64EncodeString(byte[] bytes){
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public byte[] base64DecodeString(String string){
        return DatatypeConverter.parseBase64Binary(string);
    }
}