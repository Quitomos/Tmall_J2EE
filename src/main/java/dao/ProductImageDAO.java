package dao;

import pojo.Product;
import pojo.ProductImage;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDAO {
    public void add(ProductImage productImage) {
        String sql = "insert into productImage values(null, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, productImage.getType());
            s.setInt(2, productImage.getProduct().getId());
            s.execute();
            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) productImage.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "delete from productImage where id = " + id;
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ProductImage get(int id) {
        ProductImage productImage = null;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from productImage where id = " + id;

            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                productImage = new ProductImage();
                productImage.setProduct(new ProductDAO().get(rs.getInt("pid")));
                productImage.setId(rs.getInt("id"));
                productImage.setType(rs.getString("type"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return productImage;
    }

    public List<ProductImage> list(Product product, String type) {
        return list(product, type, 0, Short.MAX_VALUE);
    }
    public List<ProductImage> list(Product product, String type, int start, int count) {
        List<ProductImage> ret = new ArrayList<>();
        String sql = "select * from productImage where pid = ? and type = ? order by id limit ?, ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, product.getId());
            s.setString(2, type);
            s.setInt(3, start);
            s.setInt(4, count);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                ProductImage p = new ProductImage();
                p.setType(type);
                //p.setProduct(new ProductDAO().get(rs.getInt("pid")));
                p.setId(rs.getInt("id"));
                ret.add(p);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public void update(ProductImage p) {
        String sql = "update productImage set type = ?, pid = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(3, p.getId());
            s.setString(1, p.getType());
            s.setInt(2, p.getProduct().getId());
            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
