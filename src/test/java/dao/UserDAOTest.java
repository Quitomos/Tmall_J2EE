package dao;

import org.junit.Test;
import pojo.User;

public class UserDAOTest {
    @Test
    public void testUserDAO() {
        UserDAO dao = new UserDAO();
        User user = new User();
        user.setName("Quitomos");
        user.setPassword("woshiquitomos");
        dao.add(user);
        dao.add(user);
        dao.get(1);
        dao.get("Quitomos");
        dao.getTotal();
        dao.delete(2);
        dao.isExist("Quitomos");
        dao.list();
        dao.update(user);
    }
}
