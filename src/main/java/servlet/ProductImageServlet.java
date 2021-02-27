package servlet;

import org.apache.commons.lang3.StringUtils;
import pojo.Product;
import pojo.ProductImage;
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

public class ProductImageServlet extends BaseBackServlet {
    @Override
    public void add(HttpServletRequest request, HttpServletResponse response, Page page) {
        Map<String, String> params = new HashMap<>();
        InputStream is = super.paraseUpload(request, params);

        String type = params.get("type");
        type = StringUtils.substringAfterLast(type, "_");
        int pid = Integer.parseInt(params.get("pid"));
        Product p = productDAO.getWithoutImgList(pid);
        ProductImage productImage = new ProductImage();
        productImage.setProduct(p);
        productImage.setType(type);
        productImageDAO.add(productImage);

        String imageFolderPath = null;
        switch (type) {
            case "single": imageFolderPath = request.getServletContext().getRealPath("img/productSingle");break;
            case "detail": imageFolderPath = request.getServletContext().getRealPath("img/productDetail");break;
            default: break;
        }
        File imageFolder = new File(imageFolderPath);
        File file = new File(imageFolder, productImage.getId() + ".jpg");
        file.getParentFile().mkdirs();

        try {
            if (null != is && 0 != is.available()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] b = new byte[1024 * 10240];
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

        super.redirect(response, "admin_productImage_list?pid=" + pid);
    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        ProductImage productImage = productImageDAO.get(id);
        String type = productImage.getType();
        int pid = productImage.getProduct().getId();
        productImageDAO.delete(id);
        String imgFolderPath = null;
        switch (type) {
            case "single" : imgFolderPath = request.getServletContext().getRealPath("img/productSingle"); break;
            case "detail" : imgFolderPath = request.getServletContext().getRealPath("img/productDetail"); break;
        }
        File img = new File(imgFolderPath, id + ".jpg");
        //System.out.println(img.getPath());
        img.delete();

        super.redirect(response, "admin_productImage_list?pid=" + pid);
    }

    @Override
    public void update(HttpServletRequest request, HttpServletResponse response, Page page) {

    }

    @Override
    public void edit(HttpServletRequest request, HttpServletResponse response, Page page) {

    }

    @Override
    public void list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product product = productDAO.get(pid);
        List<ProductImage> singleImages = product.getProductSingleImages();
        List<ProductImage> detailImages = product.getProductDetailImages();

        request.setAttribute("pisSingle", singleImages);
        request.setAttribute("pisDetail", detailImages);
        request.setAttribute("p", product);
        super.dispatcher(request, response, "/admin/listProductImage.jsp");
    }
}
