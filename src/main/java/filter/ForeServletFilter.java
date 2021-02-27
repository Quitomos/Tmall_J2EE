package filter;

import dao.CategoryDAO;
import org.apache.commons.lang3.StringUtils;
import pojo.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ForeServletFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        request.setAttribute("row", 8);

        //每次都重新计算的话，增加负担。若将计算任务留给业务，却提高了前后端耦合度。
        //注意要使用session里的值。
        if (null == request.getSession().getAttribute("cartTotalItemNumber")) {
            request.getSession().setAttribute("cartTotalItemNumber", 0);
        }

        if (null == request.getAttribute("categories")) {
            request.setAttribute("categories", new CategoryDAO().list());
        }


        String[] logoutMethod = new String[] {"home", "checkLogin", "register", "loginAjax", "login", "product",
                "category", "search"};
        Set<String> logoutMethodSet = new HashSet<>(Arrays.asList(logoutMethod));

        String servletPath = request.getServletPath();
        if (servletPath.startsWith("/fore") && !servletPath.startsWith("/foreServlet")) {
            String method = StringUtils.substringAfterLast(servletPath, "/fore");
            if (null == request.getSession().getAttribute("user") && !logoutMethodSet.contains(method))
                method = "login";
            request.setAttribute("method", method);
            request.getRequestDispatcher("/foreServlet").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
