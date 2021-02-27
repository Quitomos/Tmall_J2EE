package servlet;

import pojo.User;
import util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class UserServlet extends BaseBackServlet {
    @Override
    public void add(HttpServletRequest request, HttpServletResponse response, Page page) {

    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response, Page page) {

    }

    @Override
    public void update(HttpServletRequest request, HttpServletResponse response, Page page) {

    }

    @Override
    public void edit(HttpServletRequest request, HttpServletResponse response, Page page) {

    }

    @Override
    public void list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int total = userDAO.getTotal();
        List<User> users = userDAO.list(page.getStart(), page.getCount());
        page.setTotal(total);

        request.setAttribute("page", page);
        request.setAttribute("users", users);
        super.dispatcher(request, response, "admin/listUser.jsp");
    }
}
