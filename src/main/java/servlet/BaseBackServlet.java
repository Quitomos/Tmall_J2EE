package servlet;

import dao.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.Page;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseBackServlet extends HttpServlet {
    public abstract void add(HttpServletRequest request, HttpServletResponse response, Page page);
    public abstract void delete(HttpServletRequest request, HttpServletResponse response, Page page);
    public abstract void update(HttpServletRequest request, HttpServletResponse response, Page page);
    public abstract void edit(HttpServletRequest request, HttpServletResponse response, Page page);
    public abstract void list(HttpServletRequest request, HttpServletResponse response, Page page);

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

    public InputStream paraseUpload(HttpServletRequest request, Map<String, String> params) {
        InputStream is = null;
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            factory.setSizeThreshold((1 << 20) * 10);

            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    System.out.println("上传文件：获取输入流");
                    is = item.getInputStream();
                }
                else {
                    System.out.println("上传文件:不是文件，获取表单");

                    String paramName = item.getFieldName();
                    String paramValue = item.getString();
                    paramValue = new String(paramValue.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    System.out.println(paramName + ":" + paramValue);
                    params.put(paramName, paramValue);
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
}
