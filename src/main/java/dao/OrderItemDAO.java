package dao;

import pojo.Order;
import pojo.OrderItem;
import pojo.Product;
import pojo.User;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {
    public void add(OrderItem orderItem) {
        String sql = "insert into orderitem values(null, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, orderItem.getNum());
            s.setInt(2, orderItem.getProduct().getId());
            if (null != orderItem.getOrder())
                s.setInt(3, orderItem.getOrder().getId());
            else
                s.setNull(3, Types.INTEGER);
            s.setInt(4, orderItem.getUser().getId());
            s.execute();

            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) {
                orderItem.setId(rs.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "delete from orderitem where id = " + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateNum(OrderItem orderItem) {
        String sql = "update orderitem set num = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, orderItem.getNum());
            s.setInt(2, orderItem.getId());
            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateOrder(OrderItem orderItem) {
        String sql = "update orderitem set oid = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, orderItem.getOrder().getId());
            s.setInt(2, orderItem.getId());

            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<OrderItem> list(Order order) {
        List<OrderItem> ret = new ArrayList<>();
        String sql = "select * from orderitem where oid = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, order.getId());
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                OrderItem tmp = new OrderItem();
                tmp.setId(rs.getInt("id"));
                tmp.setOrder(order);
                tmp.setUser(new UserDAO().get(rs.getInt("uid")));
                tmp.setNum(rs.getInt("num"));
                tmp.setProduct(new ProductDAO().get(rs.getInt("pid")));
                ret.add(tmp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public int getCartTotal(User user) {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select sum(num) from orderitem where isnull(oid) and uid = " + user.getId();
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void fillSaleCount(Product product) {
        int count = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select num from orderitem where oid is not null and pid = " + product.getId();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                count += rs.getInt("num");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        product.setSaleCount(count);
    }

    public OrderItem getInCart(User user, Product product) {
        OrderItem orderItem = null;
        String sql = "select * from orderitem where uid = ? and pid = ? and isnull(oid)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, user.getId());
            s.setInt(2, product.getId());

            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                orderItem = new OrderItem();
                orderItem.setId(rs.getInt("id"));
                orderItem.setNum(rs.getInt("num"));
                orderItem.setProduct(product);
                orderItem.setUser(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return orderItem;
    }

    public List<OrderItem> getInCart(User user) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "select * from orderitem where uid = ? and isnull(oid)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, user.getId());

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(rs.getInt("id"));
                orderItem.setNum(rs.getInt("num"));
                Product product = new ProductDAO().get(rs.getInt("pid"));
                new ReviewDAO().fillReviewCount(product);
                fillSaleCount(product);
                orderItem.setProduct(product);
                orderItem.setUser(user);
                orderItems.add(orderItem);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return orderItems;
    }

    public OrderItem get(int id) {
        OrderItem orderItem = null;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from orderitem where id = " + id;

            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                orderItem = new OrderItem();
                orderItem.setId(id);
                Integer oid = rs.getInt("oid");
                if (null != oid) {
                    orderItem.setOrder(new OrderDAO().get(oid));
                }
                orderItem.setNum(rs.getInt("num"));
                orderItem.setUser(new UserDAO().get(rs.getInt("uid")));
                orderItem.setProduct(new ProductDAO().get(rs.getInt("pid")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return orderItem;
    }

    public void fillOrderItem(Order order) {
        order.setOrderItems(list(order));
    }
}
