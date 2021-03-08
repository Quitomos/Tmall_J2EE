package dao;

import pojo.Product;
import pojo.Property;
import pojo.PropertyValue;
import util.DBUtil;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyValueDAO {
    public void add(PropertyValue propertyValue) {
        String sql = "insert into propertyvalue values(null, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, propertyValue.getValue());
            s.setInt(2, propertyValue.getProperty().getId());
            s.setInt(3, propertyValue.getProduct().getId());
            s.execute();
            ResultSet rs = s.getGeneratedKeys();
            if (rs.next()) propertyValue.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection c = DBUtil.getConnection(); Statement s = c. createStatement()) {
            String sql = "delete from propertyvalue where id = " + id;
            s.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(PropertyValue propertyValue) {
        String sql = "update propertyvalue set value = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, propertyValue.getValue());
            s.setInt(2, propertyValue.getId());
            s.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public PropertyValue get(int id) {
        PropertyValue ret = null;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select * from propertyvalue where id = " + id;
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                ret = new PropertyValue();
                ret.setId(id);
                ret.setValue(rs.getString("value"));
                ret.setProperty(new PropertyDAO().get(rs.getInt("propertyId")));
                ret.setProduct(new ProductDAO().getWithoutImgList(rs.getInt("productId")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public PropertyValue get(Product product, Property property) {
        PropertyValue ret = null;
        String sql = "select * from propertyvalue where productId = ? and propertyId = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, product.getId());
            s.setInt(2, property.getId());

            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                ret = new PropertyValue();
                ret.setId(rs.getInt("id"));
                ret.setValue(rs.getString("value"));
                ret.setProperty(property);
                ret.setProduct(product);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public List<PropertyValue> list(Product product) {
        List<PropertyValue> propertyValues = new ArrayList<>();
        String sql = "select * from propertyvalue where productId = ? order by id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, product.getId());
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                PropertyValue propertyValue = new PropertyValue();
                propertyValue.setProperty(new PropertyDAO().get(rs.getInt("propertyId")));
                propertyValue.setValue(rs.getString("value"));
                propertyValue.setId(rs.getInt("id"));
                propertyValue.setProduct(product);
                propertyValues.add(propertyValue);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return propertyValues;
    }
}
