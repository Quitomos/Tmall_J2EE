package pojo;

public class PropertyValue {
    private int id;
    private String value;
    private Property property;
    private Product product;

    public Product getProduct() {
        return product;
    }

    public int getId() {
        return id;
    }

    public Property getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
