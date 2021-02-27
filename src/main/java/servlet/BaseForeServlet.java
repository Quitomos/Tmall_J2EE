package servlet;

import dao.*;
import util.Page;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseForeServlet extends HttpServlet {
    protected CategoryDAO categoryDAO = new CategoryDAO();
    protected OrderDAO orderDAO = new OrderDAO();
    protected OrderItemDAO orderItemDAO = new OrderItemDAO();
    protected ProductDAO productDAO = new ProductDAO();
    protected ProductImageDAO productImageDAO = new ProductImageDAO();
    protected PropertyDAO propertyDAO = new PropertyDAO();
    protected PropertyValueDAO propertyValueDAO = new PropertyValueDAO();
    protected ReviewDAO reviewDAO = new ReviewDAO();
    protected UserDAO userDAO = new UserDAO();

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            int start = 0, count = 5;
            try {
                start = Integer.parseInt(request.getParameter("page.start"));
                count = Integer.parseInt(request.getParameter("page.count"));
            }
            catch (Exception e) {
            }
            Page page = new Page(start, count);

            String method = (String) request.getAttribute("method");
            Method m = this.getClass().getMethod(method, HttpServletRequest.class, HttpServletResponse.class, Page.class);
            m.invoke(this, request, response, page);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected void redirect(HttpServletResponse response, String s) {
        try {
            response.sendRedirect(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void print(HttpServletResponse response, String s) {
        try {
            response.getWriter().print(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void dispatcher(HttpServletRequest request, HttpServletResponse response, String s) {
        try {
            request.getRequestDispatcher(s).forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
