package six.queenie.service;

import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;

public class CartInterceptor implements HandlerInterceptor {

    @Autowired
    private MembersService mService;

    private ThreadLocal<MembersBean> currentMember = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MembersBean member = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                	Integer memberId = decrypt(cookie.getValue());
                    member = mService.findByMemberId(memberId);
                    break;
                }
            }
        }

        if (member == null) {

            member = new MembersBean();
            String uuid = UUID.randomUUID().toString();
            member.setMemberAccount(uuid);
        }

        currentMember.set(member);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        MembersBean member = currentMember.get();

        if (member != null) {
            String encryptedUserKey = encrypt(member.getMemberAccount());
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, encryptedUserKey);
            cookie.setDomain("mall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");

            response.addCookie(cookie);
        }
    }

  
    private String encrypt(String string) {
        
        String value = String.valueOf(string);
        StringBuilder encryptedValue = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            encryptedValue.append((char) (c + 1)); 
        }
        return encryptedValue.toString();
    }

    private Integer decrypt(String encryptedValue) {
        StringBuilder decryptedValue = new StringBuilder();
        for (int i = 0; i < encryptedValue.length(); i++) {
            char c = encryptedValue.charAt(i);
            decryptedValue.append((char) (c - 1)); 
        }
       
        return Integer.parseInt(decryptedValue.toString());
    }


}
