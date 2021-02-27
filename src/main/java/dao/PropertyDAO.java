package dao;

import pojo.Category;
import pojo.Property;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {
    public int getTotal(int cid) {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from property where cid = " + cid;

            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) total = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void add(Property p) {
        String sql = "insert into property values(null, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, p.getName());
            s.setInt(2, p.getCategory().getId());

            s.execute();

            ResultSet rs = s.getGeneratedKeys();

            if (rs.next()) p.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Property p) {
        String sql = "update property set cid = ?, name = ? where id =  ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, p.getCategory().getId());
            s.setString(2, p.getName());
            s.setInt(3, p.getId());

            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "delete from property where id = " + id;
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Property get(int id) {
        Property p = null;

        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from property where id = " + id;

            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                p = new Property();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategory(new CategoryDAO().get(rs.getInt("cid")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return p;
    }

    public List<Property> list(int cid) {
        return list(cid, 0, Short.MAX_VALUE);
    }

    public List<Property> list(int cid, int start, int count) {
        List<Property> ret = new ArrayList<>();
        String sql = "select * from property where cid = ? order by id limit ?, ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, cid);
            s.setInt(2, start);
            s.setInt(3, count);

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Property p = new Property();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategory(new CategoryDAO().get(rs.getInt("id")));
                ret.add(p);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }
}
