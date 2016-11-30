package be.maximvdw.spigotsite;

import java.io.*;

/**
 * UserDebugging
 *
 * Created by maxim on 30-Nov-16.
 */
public class UserDebugging {
    public static String username = "";
    public static String password = "";

    static{
        BufferedReader br = null;
        try {
            if (new File("/var/lib/jenkins/credentials.txt").exists()) {
                br = new BufferedReader(new FileReader("/var/lib/jenkins/credentials.txt"));
            }else if (new File("C:\\Users\\maxim\\credentials.txt").exists()){
                br = new BufferedReader(new FileReader("C:\\Users\\maxim\\credentials.txt"));
            }else {
                br = new BufferedReader(new FileReader("D:\\maxim\\Documents\\credentials.txt"));
            }
            username = br.readLine();
            password = br.readLine();
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
}
