package dao;

import pojo.User;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from user";

            ResultSet rs = s.executeQuery(sql);

            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void add(User user) {
        String sql = "insert into user values(null, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, user.getName());
            s.setString(2, user.getPassword());

            s.execute();

            ResultSet rs = s.getGeneratedKeys();

            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(User user) {
        String sql = "update user set name=?, password=? where id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, user.getName());
            s.setString(2, user.getPassword());
            s.setInt(3, user.getId());

            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "delete from user where id = " + id;
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public User get(int id) {
        User user = null;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from user where id = " + id;
            ResultSet rs = s.executeQuery(sql);

            if (rs.next()) {
                user = new User();
                user.setId(id);
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public List<User> list() {
        return list(0, Short.MAX_VALUE);
    }

    public List<User> list(int start, int count) {
        List<User> ret = new ArrayList<>();
        String sql = "select * from user order by id limit ?, ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, start);
            s.setInt(2, count);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                User tmp = new User();
                tmp.setPassword(rs.getString("password"));
                tmp.setName(rs.getString("name"));
                tmp.setId(rs.getInt("id"));
                ret.add(tmp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public boolean isExist(String name) {
        return null != get(name);
    }

    public User get(String name) {
        User user = null;
        String sql = "select * from user where name = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, name);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setName(name);
                user.setId(rs.getInt("id"));
                user.setPassword(rs.getString("password"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    //利用该方法判断账号密码是否正确，将判断过程交给数据库，而不是服务器或客户端
    public User get(String name, String password) {
        User user = null;
        String sql = "select * from user where name = ? and password = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, name);
            s.setString(2, password);

            ResultSet rs = s.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setId(rs.getInt("id"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }
}
