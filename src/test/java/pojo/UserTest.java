package pojo;

import org.junit.Test;

public class UserTest {
    @Test
    public void testUser() {
        User user = new User();
        user.setName("张三三");
        System.out.println(user.getAnonymity());
    }
}
