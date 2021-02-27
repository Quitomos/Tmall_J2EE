package servlet;

import pojo.Order;
import util.DateUtil;
import util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public class OrderServlet extends BaseBackServlet {
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
        List<Order> orders = orderDAO.listWithOrderItems(page.getStart(), page.getCount());
        page.setTotal(orderDAO.getTotal());

        request.setAttribute("page", page);
        request.setAttribute("orders", orders);

        super.dispatcher(request, response, "admin/listOrder.jsp");
    }

    public void delivery(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Order order = orderDAO.get(id);
        order.setStatus("待收货");
        order.setDeliveryDate(new Date());
        orderDAO.update(order);
        int start = page.getStart();

        super.redirect(response, "admin_order_list" + (start == 0? "": "?page.start=" + start));
    }
}
