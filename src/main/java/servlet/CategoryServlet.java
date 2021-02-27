package servlet;

import pojo.Category;
import util.ImageUtil;
import util.Page;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryServlet extends BaseBackServlet {


    @Override
    public void add(HttpServletRequest request, HttpServletResponse response, Page page) {
        Map<String, String> params = new HashMap<>();
        InputStream is = super.paraseUpload(request, params);

        String name = params.get("name");
        Category c = new Category();
        c.setName(name);
        categoryDAO.add(c);

        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, c.getId() + ".jpg");
        file.getParentFile().mkdirs();
        //System.out.println(file.getAbsolutePath());

        try {
            if (null != is && 0 != is.available()) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte b[] = new byte[1024 * 10240];
                    int len = 0;
                    while (-1 != (len = is.read(b))) {
                        fos.write(b, 0, len);
                    }
                    fos.flush();

                    BufferedImage img = ImageUtil.file2jpg(file);
                    ImageIO.write(img, "jpg", file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.redirect(response, "admin_category_list");
    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        categoryDAO.delete(id);
        String imgFolderPath = request.getServletContext().getRealPath("/img/category");
        File img = new File(imgFolderPath, id + ".jpg");
        img.delete();
        super.redirect(response, "admin_category_list");
    }

    @Override
    public void update(HttpServletRequest request, HttpServletResponse response, Page page) {
        Map<String, String> params = new HashMap();
        InputStream is = super.paraseUpload(request, params);

        //System.out.println(params);
        String name = params.get("name");
        int id = Integer.parseInt(params.get("id"));

        Category c = new Category();
        c.setId(id);
        c.setName(name);
        categoryDAO.update(c);

        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, c.getId() + ".jpg");
        file.getParentFile().mkdirs();
        file.delete();
        
        try {
            if (null != is && 0 != is.available()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte b[] = new byte[1024 * 10240];
                    int len = 0;
                    while (-1 != (len = is.read(b))) {
                        fos.write(b, 0, len);
                    }
                    fos.flush();

                    BufferedImage img = ImageUtil.file2jpg(file);
                    ImageIO.write(img, "jpg", file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.redirect(response, "admin_category_list");
    }

    @Override
    public void edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Category c = categoryDAO.get(id);
        request.setAttribute("category", c);
        super.dispatcher(request, response, "admin/editCategory.jsp");
    }

    @Override
    public void list(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Category> categoryList = categoryDAO.list(page.getStart(), page.getCount());
        page.setTotal(categoryDAO.getTotal());

        request.setAttribute("thecs", categoryList);
        request.setAttribute("page", page);

        super.dispatcher(request, response, "admin/listCategory.jsp");
    }
}
