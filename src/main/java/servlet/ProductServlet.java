package servlet;

import pojo.Category;
import pojo.Product;
import pojo.Property;
import pojo.PropertyValue;
import util.DateUtil;
import util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductServlet extends BaseBackServlet {
    @Override
    public void add(HttpServletRequest request, HttpServletResponse response, Page page) {
        Product p = new Product();

        p.setName(request.getParameter("name"));
        p.setSubTitle(request.getParameter("subTitle"));
        p.setOriginalPrice(Float.parseFloat(request.getParameter("originalPrice")));
        p.setPromotePrice(Float.parseFloat(request.getParameter("promotePrice")));
        p.setStock(Integer.parseInt(request.getParameter("stock")));
        int cid = Integer.parseInt(request.getParameter("cid"));
        p.setCategory(categoryDAO.get(cid));
        p.setCreateDate(DateUtil.date2timestamp(new Date()));
        productDAO.add(p);

        super.redirect(response, "admin_product_list?cid=" + cid);
    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        int cid = productDAO.get(id).getCategory().getId();
        productDAO.delete(id);

        super.redirect(response, "admin_product_list?cid=" + cid);
    }

    @Override
    public void update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        int cid = Integer.parseInt(request.getParameter("cid"));
        Product product = productDAO.get(id);
        product.setName(request.getParameter("name"));
        product.setSubTitle(request.getParameter("subTitle"));
        product.setOriginalPrice(Float.parseFloat(request.getParameter("originalPrice")));
        product.setPromotePrice(Float.parseFloat(request.getParameter("promotePrice")));
        product.setStock(Integer.parseInt(request.getParameter("stock")));
        productDAO.update(product);

        super.redirect(response, "admin_product_list?cid=" + cid);
    }

    @Override
    public void edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.get(id);
        request.setAttribute("p", product);
        super.dispatcher(request, response, "/admin/editProduct.jsp");
    }

    @Override
    public void list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);
        List<Product> products = productDAO.list(c, page.getStart(), page.getCount());
        page.setTotal(productDAO.getTotal(c));

        //点击page进入list时不会传入cid，需手动写入page。
        page.setParam("&cid=" + cid);

        request.setAttribute("category", c);
        request.setAttribute("page", page);
        request.setAttribute("ps", products);
        super.dispatcher(request, response, "admin/listProduct.jsp");
    }

    public void editPropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.getWithoutImgList(pid);
        int cid = product.getCategory().getId();
        List<Property> properties = propertyDAO.list(cid);
        List<PropertyValue> prePropertyValues = propertyValueDAO.list(product);
        Set<Integer> propertySet = new HashSet<>();

        for (PropertyValue propertyValue: prePropertyValues) {
            propertySet.add(propertyValue.getProperty().getId());
        }

        for (Property property: properties) {
            if (!propertySet.contains(property.getId())) {
                PropertyValue propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValue.setValue("");
                propertyValueDAO.add(propertyValue);
            }
        }
        List<PropertyValue> newPropertyValues = propertyValueDAO.list(product);

        request.setAttribute("p", product);
        request.setAttribute("pvs", newPropertyValues);

        super.dispatcher(request, response, "admin/editProductValue.jsp");
    }

    public void updatePropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
        int propertyValueId = Integer.parseInt(request.getParameter("pvid"));
        String value = request.getParameter("value");
//        System.out.println("id:" + propertyValueId);
//        System.out.println("value:" + value);
        if ("delete".equals(value)) {
            propertyValueDAO.delete(propertyValueId);
        }
        else {
            PropertyValue propertyValue = propertyValueDAO.get(propertyValueId);
            if (propertyValue != null) {
                propertyValue.setValue(value);
                propertyValueDAO.update(propertyValue);
            }
            else {
                super.print(response, "false");
                return;
            }
        }

        super.print(response, "success");
    }
}
