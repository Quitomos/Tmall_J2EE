package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static String ip;
    private static String port;
    private static String database;
    private static String encoding;
    private static String loginName;
    private static String password;

    static {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            //使用路径加载，而不是直接写文件名，这样在junit时才可以读取到正确的路径
            input = new FileInputStream(DBUtil.class.getResource("/").getPath() + "config.properties");

            properties.load(input);
            ip = properties.getProperty("ip");
            port = properties.getProperty("port");
            database = properties.getProperty("database");
            encoding = properties.getProperty("encoding");
            loginName = properties.getProperty("loginName");
            password = properties.getProperty("password");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        //时区问题需设置参数serverTimezone
        String url = String.format("jdbc:mysql://%s:%s/%s?characterEncoding=%s&serverTimezone=UTC",
                ip, port, database, encoding);
        return DriverManager.getConnection(url, loginName, password);
    }
}
