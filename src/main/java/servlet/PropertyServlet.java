package servlet;

import pojo.Category;
import pojo.Property;
import util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PropertyServlet extends BaseBackServlet {

    @Override
    public void add(HttpServletRequest request, HttpServletResponse response, Page page) {
        Property p = new Property();
        String cidStr = request.getParameter("cid");
        //System.out.println("cid:" + cidStr);
        String name = request.getParameter("name");
        //System.out.println("name:" + name);
        int cid = Integer.parseInt(cidStr);

        p.setName(name);
        p.setCategory(categoryDAO.get(cid));

        propertyDAO.add(p);

        String redirect = "admin_property_list?cid=" + cid;
        super.redirect(response, redirect);
    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        int cid = propertyDAO.get(id).getCategory().getId();
        propertyDAO.delete(id);

        String redirect = "admin_property_list?cid=" + cid;
        super.redirect(response, redirect);
    }

    @Override
    public void update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        int cid = Integer.parseInt(request.getParameter("cid"));
        Property p = propertyDAO.get(id);
        p.setName(request.getParameter("name"));
        propertyDAO.update(p);

        String redirect = "admin_property_list?cid=" + cid;
        super.redirect(response, redirect);
    }

    @Override
    public void edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Property p = propertyDAO.get(id);
        request.setAttribute("p", p);

        super.dispatcher(request, response, "admin/editProperty.jsp");
    }

    @Override
    public void list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);
        List<Property> propertyList = propertyDAO.list(cid, page.getStart(), page.getCount());
        page.setTotal(propertyDAO.getTotal(cid));
        page.setParam("&cid=" + cid);

        request.setAttribute("ps", propertyList);
        request.setAttribute("page", page);
        request.setAttribute("category", c);
        super.dispatcher(request, response, "admin/listProperty.jsp");
    }
}
