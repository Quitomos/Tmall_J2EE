package dao;

import pojo.Product;
import pojo.Review;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ReviewDAO {
    public void add(Review review) {
        String sql = "insert into review values(null, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, review.getContent());
            s.setInt(2, review.getProduct().getId());
            s.setInt(3, review.getUser().getId());
            s.setTimestamp(4, DateUtil.date2timestamp(review.getCreateDate()));
            s.execute();
            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) review.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "delete from review where id = " + id;
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<Review> list(Product product) {
        List<Review> ret = new ArrayList<>();
        String sql = "select * from review where pid = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, product.getId());
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setProduct(product);
                review.setContent(rs.getString("content"));
                review.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                review.setUser(new UserDAO().get(rs.getInt("uid")));
                ret.add(review);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public int getTotal(Product product) {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from review where pid = " + product.getId();

            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void fillReviewCount(Product product) {
        int count = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from review where pid = " + product.getId();
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) count = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        product.setReviewCount(count);
    }
}
