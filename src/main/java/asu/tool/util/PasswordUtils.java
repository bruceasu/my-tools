/**
 * 
 */
package asu.tool.util;

public class PasswordUtils {

    private final static String SALT = "GOLDENWAY-freeib";

    public static String encodePassword(String password) {
        String p = passwordPattern(password);
        return new StringBuilder().append("md5(").append(MD5.encodeByMD5(p)).append(")").toString();
    }

    public static String encodePasswordWithNoneType(String password) {
        String p = passwordPattern(password);
        return new StringBuilder().append(MD5.encodeByMD5(p)).toString();
    }

    public static String genResetPasswordUrl(String password) {
        String p = passwordPattern(password);
        return new StringBuilder().append(MD5.encodeByMD5(p)).toString();
    }

    private static String passwordPattern(String password) {
        return password + SALT;
    }

    public static boolean checkPassword(String password, String encodedPassword) {
        if (encodePassword(password).equals(encodedPassword)) {
            return true;
        } else {
            return false;
        }
    }
}
