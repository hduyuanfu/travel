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

@WebServlet("/registUserServlet")
public class RegistUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 过滤器已经做了编码校验工作
        // 1. 获取数据
        Map<String, String[]> map = request.getParameterMap();
        // 2. 封装数据
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // 3. 调用service完成注册
        UserServiceImpl service = new UserServiceImpl();
        boolean flag = service.regist(user);

        // 把后台返回的数据封装成一个对象，把对象再序列化为json数据传给前台（前台也是把表单数据封装成一个对象，把对象传到后台）
        ResultInfo info = new ResultInfo();
        // 4. 响应结果
        if(flag) {
            // 注册成功
            info.setFlag(true);
        } else {
            // 注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败！");
        }

        // 将info对象序列化为json,再把json传给前台
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);

        // 给response设置MIME类型(这样前台浏览器可以直接解析response，不用浏览器自己判断是什么类型了)
        // 将json数据写回客户端
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json); // response.getOutputStream().write(json.getBytes());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
