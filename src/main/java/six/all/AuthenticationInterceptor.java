package six.all;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthenticationInterceptor implements HandlerInterceptor{
	
	//攔截器判斷
	 @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	        String requestUri = request.getRequestURI();
	        
	        // 如果是公開頁面，可以直接進入
	        if (requestUri.equals("/public/**")){
	            return true;
	        }
	        //或是取得session 可以進入
	        HttpSession session = request.getSession(false);
	        if (session != null && session.getAttribute("logInManagement") != null) {
	            // 已登錄，可以進入
	            return true;
	        }
	        
	        // 沒有登錄，重定向到登錄頁面
	        response.sendRedirect("/FinalProject/public/BackLoginMain");
	        return false;
	    }
	 
	}
	

