package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 获取用户名和密码数据
        Map<String, String[]> parameterMap = request.getParameterMap();
        // 2. 封装user对象
        User user = new User();
        try {
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // 3. 调用service查询
        UserServiceImpl service = new UserServiceImpl();
        User u = service.login(user);

        // 将错误信息封装成一个对象，然后对象序列化为json,再通过ajax技术与前台通信
        ResultInfo resultInfo = new ResultInfo();

        // 4. 判断用户对象是否为null
        if(u == null) {
            // 用户名或密码错误
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("用户名或密码错误");
        }

        // 5. 判断用户是否激活
        if(u != null && !"Y".equals(u.getStatus())) {
            // 用户尚未激活
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("您尚未激活，请激活");
        }

        // 6. 判断登录成功
        if(u != null && "Y".equals(u.getStatus())) {
            // 登录成功
            resultInfo.setFlag(true);
            // 为了index中可以根据用户姓名个性化提示，谁谁谁，欢迎登录。所以这里用session给findUserServlet共享这个user对象
            request.getSession().setAttribute("user", u);
        }

        // 7. 响应数据
        /*// 7.1 响应对象序列化为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(resultInfo);
        // 7.2 response设置MIME类型和字符集编码
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);*/

        // 这种方式和上面效果是一样的
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), resultInfo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
