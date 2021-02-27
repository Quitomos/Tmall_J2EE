package dao;

import pojo.Category;
import pojo.Product;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from category";

            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void add(Category category) {
        String sql = "insert into category values(null, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, category.getName());

            s.execute();

            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Category category) {
        String sql = "update category set name=? where id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, category.getName());
            s.setString(2, Integer.toString(category.getId()));

            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "delete from category where id = " + id;
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Category get(int id) {
        Category category = null;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from category where id =" + id;

            ResultSet rs = s.executeQuery(sql);

            if (rs.next()) {
                category = new Category();
                category.setId(id);
                category.setName(rs.getString("name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return category;
    }

    public Category get(String name) {
        Category category = null;
        String sql = "select * from category where name = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, name);
            ResultSet rs = s.executeQuery();

            if (rs.next()) {
                category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(name);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return category;
    }

    public List<Category> list() {
        return list(0, Short.MAX_VALUE);
    }

    public List<Category> list(int start, int count) {
        List<Category> ret = new ArrayList<>();

        String sql = "select * from category order by id limit ?, ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, start);
            s.setInt(2, count);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                Category tmp = new Category();
                tmp.setName(rs.getString(2));
                tmp.setId(rs.getInt(1));
                ret.add(tmp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    //该方法放在ProductDAO里会比较好，避免了死循环调用的可能。
    //放在ProductDAO里，使用装饰模式会更加合理。
    public List<Category> listWithProducts(int row) {
        List<Category> categories = list();
        for (Category category: categories) {
            List<Product> products = new ProductDAO().list(category);
            category.setProducts(products);
            List<List<Product>> productsByRow = new ArrayList<>();
            int len = products.size();
            for (int l = 0; l < len; l += row) {
                int r = Math.min(len, l + row);
                productsByRow.add(products.subList(l, r));
            }
            category.setProductsByRow(productsByRow);
        }
        return categories;
    }
}
