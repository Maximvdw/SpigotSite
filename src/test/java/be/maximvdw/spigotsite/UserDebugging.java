package be.maximvdw.spigotsite;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.api.user.exceptions.TwoFactorAuthenticationException;

import java.io.*;

/**
 * UserDebugging
 * <p>
 * Created by maxim on 30-Nov-16.
 */
public class UserDebugging {
    public static String username = "";
    public static String password = "";
    public static String totpSecret = "";
    private static User user = null;

    static {
        BufferedReader br = null;
        try {
            if (new File("/var/lib/jenkins/credentials.txt").exists()) {
                br = new BufferedReader(new FileReader("/var/lib/jenkins/credentials.txt"));
            } else if (new File("C:\\Users\\maxim\\credentials.txt").exists()) {
                br = new BufferedReader(new FileReader("C:\\Users\\maxim\\credentials.txt"));
            } else {
                br = new BufferedReader(new FileReader("D:\\maxim\\Documents\\credentials.txt"));
            }
            username = br.readLine();
            password = br.readLine();
            totpSecret = br.readLine();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static User getUser() throws InvalidCredentialsException, TwoFactorAuthenticationException {
        if (user != null)
            return user;
        UserManager userManager = SpigotSite.getAPI().getUserManager();
        // Log in
        user = userManager.authenticate(UserDebugging.username, UserDebugging.password,UserDebugging.totpSecret);
        return user;
    }
}
