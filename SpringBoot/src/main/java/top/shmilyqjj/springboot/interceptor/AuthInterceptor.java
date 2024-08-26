package top.shmilyqjj.springboot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.shmilyqjj.springboot.models.entity.User;

/**
 * @author Shmily
 * @Description:
 * @CreateTime: 2024/8/26 下午9:12
 */

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if ("666666".equals(authorization)) {
            response.setStatus(401);
            // 返回错误信息
            response.getWriter().write("Unauthorized, because Authorization=666666");
            return false;
        } else {
            // 拦截器设置模拟用户信息
            request.setAttribute("userInfo", new User("1", "Shmily", 26));
            return true;
        }
    }
}
