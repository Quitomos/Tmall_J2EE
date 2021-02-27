package pojo;

import java.util.Date;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private String subTitle;
    private float originalPrice;
    private float promotePrice;
    private int stock;
    private Date createDate;
    private Category category;
    private List<ProductImage> productSingleImages;
    private List<ProductImage> productDetailImages;
    private int reviewCount;
    private int saleCount;

    public ProductImage getFirstProductImage() {
        if (null == productSingleImages || 0 == productSingleImages.size()) return null;
        return productSingleImages.get(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public float getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(float promotePrice) {
        this.promotePrice = promotePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Category getCategory() {
        return category;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public List<ProductImage> getProductDetailImages() {
        return productDetailImages;
    }

    public List<ProductImage> getProductSingleImages() {
        return productSingleImages;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setProductDetailImages(List<ProductImage> productDetailImages) {
        this.productDetailImages = productDetailImages;
    }

    public void setProductSingleImages(List<ProductImage> productSingleImages) {
        this.productSingleImages = productSingleImages;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }
}
