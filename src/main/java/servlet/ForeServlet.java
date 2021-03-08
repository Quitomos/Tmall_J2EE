package servlet;

import pojo.*;
import util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ForeServlet extends BaseForeServlet {
    public void home(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Category> categories = categoryDAO.listWithProducts((Integer)request.getAttribute("row"));
        request.setAttribute("categories", categories);

        super.dispatcher(request, response, "home.jsp");
    }

    public void register(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        if (userDAO.isExist(name)) {
            request.setAttribute("msg", "用户名已存在！");
            super.dispatcher(request, response, "register.jsp");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        userDAO.add(user);

        super.redirect(response, "registerSuccess.jsp");
    }

    //重定向的路径加"/"表示绝对路径，没有项目名
    public void login(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        User user = userDAO.get(name, password);
        if (null == user) {
            request.setAttribute("msg", "用户名或密码错误！");
            super.dispatcher(request, response, "login.jsp");
            return;
        }

        request.getSession().setAttribute("user", user);
        request.getSession().setAttribute("cartTotalItemNumber", orderItemDAO.getCartTotal(user));

        super.redirect(response, "forehome");
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, Page page) {
        request.getSession().invalidate();

        super.redirect(response, "forehome");
    }

    public void product(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product product = productDAO.get(pid);
        orderItemDAO.fillSaleCount(product);
        reviewDAO.fillReviewCount(product);
        List<Review> reviews = reviewDAO.list(product);
        List<PropertyValue> propertyValues = propertyValueDAO.list(product);

        request.setAttribute("p", product);
        request.setAttribute("reviews", reviews);
        request.setAttribute("pvs", propertyValues);

        super.dispatcher(request, response, "product.jsp");
    }

    public void checkLogin(HttpServletRequest request, HttpServletResponse response, Page page) {
        if (null == request.getSession().getAttribute("user"))
            super.print(response, "false");
        else super.print(response, "success");
    }

    public void addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product product = productDAO.get(pid);
        User user = (User)request.getSession().getAttribute("user");

        OrderItem orderItem = orderItemDAO.getInCart(user, product);
        if (null == orderItem) {
            orderItem = new OrderItem();
            orderItem.setNum(num);
            orderItem.setProduct(productDAO.get(pid));
            orderItem.setUser((User) request.getSession().getAttribute("user"));
            orderItemDAO.add(orderItem);
        }
        else {
            orderItem.setNum(orderItem.getNum() + num);
            orderItemDAO.updateNum(orderItem);
        }

        request.getSession().setAttribute("cartTotalItemNumber", orderItemDAO.getCartTotal(user));
        super.print(response, "success");
    }

    //orderItem未存入数据库
    public void buyone(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product product = productDAO.get(pid);

        OrderItem orderItem = new OrderItem();
        orderItem.setNum(num);
        orderItem.setProduct(product);
        orderItem.setUser((User)request.getSession().getAttribute("user"));

        float total = num * product.getPromotePrice();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        request.getSession().setAttribute("orderItems", orderItems);
        request.setAttribute("total", total);

        super.dispatcher(request, response, "buy.jsp");
    }

    public void loginAjax(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        User user = userDAO.get(name, password);

        if (null == user) {
            super.print(response, "fail");
        }
        else {
            request.getSession().setAttribute("user", user);
            request.getSession().setAttribute("cartTotalItemNumber", orderItemDAO.getCartTotal(user));
            super.print(response, "success");
        }
    }

    public void category(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category category = categoryDAO.get(cid);
        String sort = request.getParameter("sort");
        productDAO.fillProducts(category);
        List<Product> products = category.getProducts();
        if (null == sort) sort = "all";
        switch (sort) {
            case "review":
                Collections.sort(products, (p1, p2) -> p2.getReviewCount() - p1.getReviewCount());
                break;
            case "date":
                Collections.sort(products, (p1, p2) -> p2.getCreateDate().compareTo(p1.getCreateDate()));
                break;
            case "saleCount":
                Collections.sort(products, (p1, p2) -> p2.getSaleCount() - p1.getSaleCount());
                break;
            case "price":
                Collections.sort(products, (p1, p2) -> (int) (p1.getPromotePrice() - p2.getPromotePrice()));
                break;
            case "all":
            default:
                Collections.sort(products, (p1, p2) -> p2.getSaleCount() * p2.getReviewCount() - p1.getReviewCount() * p1.getReviewCount());
        }

        request.setAttribute("category", category);
        super.dispatcher(request, response, "category.jsp");
    }

    public void search(HttpServletRequest request, HttpServletResponse response, Page page) {
        String keyword = request.getParameter("keyword");
        List<Product> products = productDAO.listByKeyword(keyword);

        request.setAttribute("ps", products);
        super.dispatcher(request, response, "searchResult.jsp");
    }

    public void cart(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User)request.getSession().getAttribute("user");
        List<OrderItem> orderItems = orderItemDAO.getInCart(user);

        request.setAttribute("orderItems", orderItems);
        super.dispatcher(request, response, "cart.jsp");
    }

    public void deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("orderItem"));
        orderItemDAO.delete(oid);
        request.getSession().setAttribute(
                "cartTotalItemNumber",
                orderItemDAO.getCartTotal(
                        (User)request.getSession().getAttribute("user")
                ));

        super.print(response, "success");
    }

    public void changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("number"));
        User user = (User) request.getSession().getAttribute("user");
        Product product = productDAO.get(pid);
        orderItemDAO.fillSaleCount(product);
        reviewDAO.fillReviewCount(product);

        OrderItem orderItem = orderItemDAO.getInCart(user, product);
        orderItem.setNum(num);
        orderItemDAO.updateNum(orderItem);
        request.getSession().setAttribute(
                "cartTotalItemNumber",
                orderItemDAO.getCartTotal(
                        (User)request.getSession().getAttribute("user")
                ));

        super.print(response, "success");
    }

    public void buy(HttpServletRequest request, HttpServletResponse response, Page page) {
        String[] oiIds = request.getParameterValues("orderItem");
        List<OrderItem> orderItems = new ArrayList<>();

        float total = 0;
        for (String oiId: oiIds) {
            OrderItem orderItem = orderItemDAO.get(Integer.parseInt(oiId));
            total += orderItem.getNum() * orderItem.getProduct().getPromotePrice();
            orderItems.add(orderItem);
        }

        request.getSession().setAttribute("orderItems", orderItems);
        request.setAttribute("total", total);
        super.dispatcher(request, response, "buy.jsp");
    }

    public void createOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        String address = request.getParameter("address");
        String post = request.getParameter("post");
        String receiver = request.getParameter("receiver");
        String mobile = request.getParameter("mobile");
        String userMessage = request.getParameter("userMessage");

        User user = (User)request.getSession().getAttribute("user");

        Order order = new Order();
        order.setOrderCode(user.getId() + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        order.setAddress(address);
        order.setPost(post);
        order.setReceiver(receiver);
        order.setMobile(mobile);
        order.setUserMessage(userMessage);
        order.setCreateDate(new Date());
        order.setStatus("待付款");
        order.setUser(user);
        orderDAO.add(order);

        int total = 0;
        List<OrderItem> orderItems = (List<OrderItem>) request.getSession().getAttribute("orderItems");
        for (OrderItem orderItem: orderItems) {
            orderItem.setOrder(order);
            if (null == (Integer)orderItem.getId()) orderItemDAO.add(orderItem);
            else orderItemDAO.updateOrder(orderItem);
            int num = orderItem.getNum();
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() - num);
            productDAO.update(product);
            total += num * product.getPromotePrice();
        }

        request.getSession().setAttribute("cartTotalItemNumber", orderItemDAO.getCartTotal(user));

        super.redirect(response, "forealipay?oid=" + order.getId() + "&total=" + total);
    }

    public void alipay(HttpServletRequest request, HttpServletResponse response, Page page) {
        super.dispatcher(request, response, "alipay.jsp");
    }

    public void payed(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order order = orderDAO.get(oid);
        order.setPayDate(new Date());
        orderDAO.updatePayDate(order);

        request.setAttribute("o", order);
        super.dispatcher(request, response, "payed.jsp");
    }

    public void bought(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User)request.getSession().getAttribute("user");
        List<Order> orders = orderDAO.listWithOrderItems(user);

        request.setAttribute("os", orders);
        super.dispatcher(request, response, "bought.jsp");
    }

    public void deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));

        orderDAO.delete(oid);

        super.print(response, "success");
    }

    public void confirmPay(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order order = orderDAO.get(oid);
        order.setStatus("待评价");
        order.setConfirmDate(new Date());

        orderDAO.update(order);

        super.redirect(response, "forebought");
    }

    public void review(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order order = orderDAO.get(oid);
        orderItemDAO.fillOrderItem(order);

        //根据前端只能选择一个商品进行评价。
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewDAO.list(product);

        reviewDAO.fillReviewCount(product);
        orderItemDAO.fillSaleCount(product);

        request.setAttribute("p", product);
        request.setAttribute("o", order);
        request.setAttribute("reviews", reviews);

        super.dispatcher(request, response, "review.jsp");
    }

    public void doReview(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        int pid = Integer.parseInt(request.getParameter("pid"));
        String content = request.getParameter("content");

        Order order = orderDAO.get(oid);
        order.setStatus("已完成");
        orderDAO.update(order);

        Review review = new Review();
        review.setContent(content);
        review.setProduct(productDAO.getWithoutImgList(pid));
        review.setUser(order.getUser());
        review.setCreateDate(new Date());
        reviewDAO.add(review);

        super.redirect(response, "forereview?oid=" + oid + "&showonly=true");
    }
}
