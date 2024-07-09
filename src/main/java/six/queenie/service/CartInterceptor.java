package six.queenie.service;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.pinhong.model.Product;

public class CartInterceptor implements HandlerInterceptor {

   
    @Autowired
    private CartService cartService;
    
    @Autowired 
    private MembersRepository mRepo;
   

    private ThreadLocal<MembersBean> currentMember = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MembersBean member = null;
        boolean isLoggedIn = false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            member = (MembersBean) authentication.getPrincipal();
            isLoggedIn = true;
        }

        if (!isLoggedIn) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                        String tempOrderAccount = decrypt(cookie.getValue());
                        member = mRepo.findByMemberAccount(tempOrderAccount);
                        break;
                    }
                }
            }

            if (member == null) {
            
            	member = new MembersBean();
                String uuid = UUID.randomUUID().toString(); 
                member.setMemberAccount(uuid);
            }
        } else {
        	 HttpSession session = request.getSession();
            List<Product> cartItems = (List<Product>) session.getAttribute(CartConstant.SESSION_CART);
            if (cartItems != null) {
            
                cartService.orderCheckedOut(member, cartItems, true, session); 
                session.removeAttribute(CartConstant.SESSION_CART);
            }
        }

        currentMember.set(member);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        currentMember.remove();
    }

    private String decrypt(String value) {
    
        return value; 
    }
}
