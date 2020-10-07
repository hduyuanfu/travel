package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.service.impl.CategoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/category/*")
public class CategoryServlet extends BaseServlet {

    // 声明UserService业务对象，下面的方法就不用每个都声明该对象
    private CategoryService service = new CategoryServiceImpl();

    /**
     * 查询所有
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1.调用service查询所有
        List<Category> cs = service.findAll();
        /*// 2.序列化为json并返回
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(cs);

        // 3.设置response的返回值MIME类型，这样浏览器可以直接解析，不用判断，以防乱码
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);*/

        /*// 下面这种方式是和上面的2，3等价的
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), cs);*/
        writeValue(cs, response);
    }
}
