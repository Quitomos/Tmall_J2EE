package dao;

import pojo.Category;
import pojo.Product;
import pojo.Review;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private ProductImageDAO productImageDAO = new ProductImageDAO();

    public Product get(int id) {
        Product product = null;
        String sql = "select * from product where id = " + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                product = new Product();
                product.setId(id);
                product.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                product.setName(rs.getString("name"));
                product.setSubTitle(rs.getString("subTitle"));
                product.setOriginalPrice(rs.getFloat("originalPrice"));
                product.setPromotePrice(rs.getFloat("promotePrice"));
                product.setStock(rs.getInt("stock"));
                product.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));

                //这两个同样写在productImageDAO里用装饰模式比较好。
                product.setProductSingleImages(productImageDAO.list(product, "single"));
                product.setProductDetailImages(productImageDAO.list(product, "detail"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return product;
    }

    public Product getWithoutImgList(int id) {
        Product product = null;
        String sql = "select * from product where id = " + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                product = new Product();
                product.setId(id);
                product.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                product.setName(rs.getString("name"));
                product.setSubTitle(rs.getString("subTitle"));
                product.setOriginalPrice(rs.getFloat("originalPrice"));
                product.setPromotePrice(rs.getFloat("promotePrice"));
                product.setStock(rs.getInt("stock"));
                product.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return product;
    }

    public Product get(String name) {
        Product product = null;
        String sql = "select * from product where name = " + name;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                product.setName(name);
                product.setSubTitle(rs.getString("subTitle"));
                product.setOriginalPrice(rs.getFloat("originalPrice"));
                product.setPromotePrice(rs.getFloat("promotePrice"));
                product.setStock(rs.getInt("stock"));
                product.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                product.setProductSingleImages(productImageDAO.list(product, "single"));
                product.setProductDetailImages(productImageDAO.list(product, "detail"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return product;
    }

    public void add(Product product) {
        String sql = "insert into product values(null, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, product.getName());
            s.setString(2, product.getSubTitle());
            s.setFloat(3, product.getOriginalPrice());
            s.setFloat(4, product.getPromotePrice());
            s.setInt(5, product.getStock());
            s.setTimestamp(6, DateUtil.date2timestamp(product.getCreateDate()));
            s.setInt(7, product.getCategory().getId());
            s.execute();

            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) product.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "delete from product where id =" + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Product product) {
        String sql = "update product set name = ?, subTitle = ?, originalPrice = ?, promotePrice = ?, stock = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, product.getName());
            s.setString(2, product.getSubTitle());
            s.setFloat(3, product.getOriginalPrice());
            s.setFloat(4, product.getPromotePrice());
            s.setInt(5, product.getStock());
            s.setInt(6, product.getId());

            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<Product> list(Category c) {
        return list(c, 0, Short.MAX_VALUE);
    }

    public List<Product> list(Category category, int start, int count) {
        List<Product> products = new ArrayList<>();
        String sql = "select * from product where cid = ? order by id limit ?, ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, category.getId());
            s.setInt(2, start);
            s.setInt(3, count);

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                product.setName(rs.getString("name"));
                product.setSubTitle(rs.getString("subTitle"));
                product.setOriginalPrice(rs.getFloat("originalPrice"));
                product.setPromotePrice(rs.getFloat("promotePrice"));
                product.setStock(rs.getInt("stock"));
                product.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                product.setProductSingleImages(productImageDAO.list(product, "single"));
                product.setProductDetailImages(productImageDAO.list(product, "detail"));
                products.add(product);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return products;
    }

    public int getTotal(Category category) {
        int total = 0;
        String sql = "select count(*) from product where cid = " + category.getId();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void fillProducts(Category category) {
        category.setProducts(list(category));
        for (Product product: category.getProducts()) {
            new OrderItemDAO().fillSaleCount(product);
            new ReviewDAO().fillReviewCount(product);
        }
    }

    public List<Product> listByKeyword(String keyword) {
        List<Product> products = new ArrayList<>();
        if (null == keyword) return products;
        keyword = keyword.trim();
        if (0 == keyword.length()) return products;

        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from product where name like '%" + keyword + "%'";
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                product.setName(rs.getString("name"));
                product.setSubTitle(rs.getString("subTitle"));
                product.setOriginalPrice(rs.getFloat("originalPrice"));
                product.setPromotePrice(rs.getFloat("promotePrice"));
                product.setStock(rs.getInt("stock"));
                product.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                product.setProductSingleImages(productImageDAO.list(product, "single"));
                product.setProductDetailImages(productImageDAO.list(product, "detail"));
                new OrderItemDAO().fillSaleCount(product);
                new ReviewDAO().fillReviewCount(product);
                products.add(product);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return products;
    }
}
