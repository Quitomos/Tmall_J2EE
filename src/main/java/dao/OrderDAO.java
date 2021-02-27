package dao;

import pojo.Order;
import pojo.OrderItem;
import pojo.User;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from `order`";
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void add(Order order) {
        String sql = "insert into `order` values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, order.getOrderCode());
            String address = order.getAddress();
            if (null != address) s.setString(2, address);
            else s.setNull(2, Types.VARCHAR);
            String post = order.getPost();
            if (null != post) s.setString(3, post);
            else s.setNull(3, Types.VARCHAR);
            String receiver = order.getReceiver();
            if (null != receiver) s.setString(4, receiver);
            else s.setNull(4, Types.VARCHAR);
            String mobile = order.getMobile();
            if (null != mobile) s.setString(5, mobile);
            else s.setNull(5, Types.VARCHAR);
            String userMessage = order.getUserMessage();
            if (null != userMessage) s.setString(6, userMessage);
            else s.setNull(6, Types.VARCHAR);
            s.setTimestamp(7, DateUtil.date2timestamp(order.getCreateDate()));
            Date payDate = order.getPayDate();
            if (null != payDate) s.setTimestamp(8, DateUtil.date2timestamp(payDate));
            else s.setNull(8, Types.TIMESTAMP);
            Date deliverDate = order.getDeliveryDate();
            if (null != deliverDate) s.setTimestamp(9, DateUtil.date2timestamp(deliverDate));
            else s.setNull(9, Types.TIMESTAMP);
            Date confirmDate = order.getConfirmDate();
            if (null != confirmDate) s.setTimestamp(10, DateUtil.date2timestamp(confirmDate));
            else s.setNull(10, Types.TIMESTAMP);
            s.setString(11, order.getStatus());
            s.setInt(12, order.getUser().getId());

            s.execute();
            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) order.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.list(get(id));
        for (OrderItem ordreItem: orderItems) {
            orderItemDAO.delete(ordreItem.getId());
        }
        String sql = "delete from `order` where id = " + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Order order) {
        String sql = "update `order` set orderCode = ?, address = ?, post = ?, receiver = ?, mobile = ?, " +
                "userMessage = ?, createDate = ?, payDate = ?, deliveryDate = ?, confirmDate = ?, status = ?, uid = ? " +
                "where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, order.getOrderCode());
            s.setString(2, order.getAddress());
            s.setString(3, order.getPost());
            s.setString(4, order.getReceiver());
            s.setString(5, order.getMobile());
            s.setString(6, order.getUserMessage());
            s.setTimestamp(7, DateUtil.date2timestamp(order.getCreateDate()));
            s.setTimestamp(8, DateUtil.date2timestamp(order.getPayDate()));
            s.setTimestamp(9, DateUtil.date2timestamp(order.getDeliveryDate()));
            s.setTimestamp(10, DateUtil.date2timestamp(order.getConfirmDate()));
            s.setString(11, order.getStatus());
            s.setInt(12, order.getUser().getId());
            s.setInt(13, order.getId());

            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Order get(int id) {
        Order order = null;
        //System.out.println(id);
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from `order` where id = " + id;
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderCode(rs.getString("orderCode"));
                order.setAddress(rs.getString("address"));
                order.setConfirmDate(DateUtil.timestamp2date(rs.getTimestamp("confirmDate")));
                order.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                order.setMobile(rs.getString("mobile"));
                order.setDeliveryDate(DateUtil.timestamp2date(rs.getTimestamp("deliveryDate")));
                order.setPayDate(DateUtil.timestamp2date(rs.getTimestamp("payDate")));
                order.setPost(rs.getString("post"));
                order.setReceiver(rs.getString("receiver"));
                order.setUserMessage(rs.getString("userMessage"));
                order.setUser(new UserDAO().get(rs.getInt("uid")));
                order.setStatus(rs.getString("status"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return order;
    }

    public List<Order> list() {
        return list(0, Short.MAX_VALUE);
    }

    public List<Order> list(int start, int count) {
        List<Order> ret = new ArrayList<>();
        String sql = "select * from `order` order by id limit ?, ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, start);
            s.setInt(2, count);

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderCode(rs.getString("orderCode"));
                order.setAddress(rs.getString("address"));
                order.setConfirmDate(DateUtil.timestamp2date(rs.getTimestamp("confirmDate")));
                order.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                order.setMobile(rs.getString("mobile"));
                order.setDeliveryDate(DateUtil.timestamp2date(rs.getTimestamp("deliveryDate")));
                order.setPayDate(DateUtil.timestamp2date(rs.getTimestamp("payDate")));
                order.setPost(rs.getString("post"));
                order.setReceiver(rs.getString("receiver"));
                order.setUserMessage(rs.getString("userMessage"));
                order.setUser(new UserDAO().get(rs.getInt("uid")));
                order.setStatus(rs.getString("status"));
                ret.add(order);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public List<Order> listWithOrderItems() {
        return listWithOrderItems(0, Short.MAX_VALUE);
    }

    public List<Order> listWithOrderItems(int start, int count) {
        List<Order> ret = new ArrayList<>();
        String sql = "select * from `order` order by id limit ?, ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, start);
            s.setInt(2, count);

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderCode(rs.getString("orderCode"));
                order.setAddress(rs.getString("address"));
                order.setConfirmDate(DateUtil.timestamp2date(rs.getTimestamp("confirmDate")));
                order.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                order.setMobile(rs.getString("mobile"));
                order.setDeliveryDate(DateUtil.timestamp2date(rs.getTimestamp("deliveryDate")));
                order.setPayDate(DateUtil.timestamp2date(rs.getTimestamp("payDate")));
                order.setPost(rs.getString("post"));
                order.setReceiver(rs.getString("receiver"));
                order.setUserMessage(rs.getString("userMessage"));
                order.setUser(new UserDAO().get(rs.getInt("uid")));
                order.setStatus(rs.getString("status"));

                List<OrderItem> orderItems = new OrderItemDAO().list(order);
                order.setOrderItems(orderItems);

                float total = 0;
                int num = 0;
                for (OrderItem orderItem: orderItems) {
                    int curNum = orderItem.getNum();
                    float curPrice = curNum * (orderItem.getProduct().getPromotePrice());
                    total += curPrice;
                    num += curNum;
                }

                order.setTotalNumber(num);
                order.setTotal(total);
                ret.add(order);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public void updatePayDate(Order order) {
        String sql = "update `order` set payDate = ?, status = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setTimestamp(1, DateUtil.date2timestamp(new Date()));
            s.setString(2, "待发货");
            s.setInt(3, order.getId());

            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<Order> listWithOrderItems(User user) {
        List<Order> ret = new ArrayList<>();
        String sql = "select * from `order` where uid = ? order by id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, user.getId());
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderCode(rs.getString("orderCode"));
                order.setAddress(rs.getString("address"));
                order.setConfirmDate(DateUtil.timestamp2date(rs.getTimestamp("confirmDate")));
                order.setCreateDate(DateUtil.timestamp2date(rs.getTimestamp("createDate")));
                order.setMobile(rs.getString("mobile"));
                order.setDeliveryDate(DateUtil.timestamp2date(rs.getTimestamp("deliveryDate")));
                order.setPayDate(DateUtil.timestamp2date(rs.getTimestamp("payDate")));
                order.setPost(rs.getString("post"));
                order.setReceiver(rs.getString("receiver"));
                order.setUserMessage(rs.getString("userMessage"));
                order.setUser(new UserDAO().get(rs.getInt("uid")));
                order.setStatus(rs.getString("status"));

                List<OrderItem> orderItems = new OrderItemDAO().list(order);
                order.setOrderItems(orderItems);

                float total = 0;
                int num = 0;
                for (OrderItem orderItem: orderItems) {
                    int curNum = orderItem.getNum();
                    float curPrice = curNum * (orderItem.getProduct().getPromotePrice());
                    total += curPrice;
                    num += curNum;
                }

                order.setTotalNumber(num);
                order.setTotal(total);
                ret.add(order);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }
}
