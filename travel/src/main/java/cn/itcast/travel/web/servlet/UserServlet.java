package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

    // 声明UserService业务对象，下面的方法就不用每个都声明该对象
    private UserService service = new UserServiceImpl();

    /**
     * 注册功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 验证码校验
        String check = request.getParameter("check");
        // 从session中获取验证码
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        // 一旦从session中获取了验证码之后，就删除session中的验证码，防止验证码复用
        session.removeAttribute("CHECKCODE_SERVER");

        // 比较
        if(checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)) {
            // 验证码错误
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误！");
            // 将info对象序列化为json,再把json传给前台
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);

            // 给response设置MIME类型(这样前台浏览器可以直接解析response，不用浏览器自己判断是什么类型了)
            // 将json数据写回客户端
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json); // response.getOutputStream().write(json.getBytes());
            return;
        }

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
//        UserService service = new UserServiceImpl();
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

    /**
     * 登录功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
//        UserServiceImpl service = new UserServiceImpl();
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

    /**
     * 查询单个对象
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 从session中获取登录用户
        Object user = request.getSession().getAttribute("user");
        // 将user写回客户端

        /*ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), user);*/
        writeValue(user, response);
    }

    /**
     * 退出功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 销毁session
        request.getSession().invalidate();

        // 2. 跳转页面
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    /**
     * 激活功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 获取激活码
        String code = request.getParameter("code");
        if(code != null) {
            // 2. 调用service完成激活
//            UserService service = new UserServiceImpl(); // 我们玩的就是多态，用这个可以添加约束
            boolean flag = service.active(code);

            // 3. 判断标记
            String msg = null;
            if(flag) {
                // 激活成功
                msg = "激活成功，请<a href='login.html'>登录</a>";
            } else {
                // 激活失败
                msg = "激活失败，请联系管理员";
            }
            // 设置一下response的响应MIME类型和字符编码，防止前台乱码
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);;
        }
    }
}
