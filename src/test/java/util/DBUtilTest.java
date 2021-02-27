package util;

import org.junit.Test;

import java.sql.SQLException;

public class DBUtilTest {
    @Test
    public void testDBUtil() throws SQLException {
        System.out.println(DBUtil.getConnection());
    }
}
