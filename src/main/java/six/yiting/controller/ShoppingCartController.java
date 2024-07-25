package six.yiting.controller;

/*
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes("cartItems")
public class ShoppingCartController {

    @ModelAttribute("cartItems")
    public List<String> initCart() {
        return new ArrayList<>();
    }

    @GetMapping("/yiting/cart")
    public String viewCart(Model model) {
        return "back/yiting/test2";
    }

    @PostMapping("/yiting/cart/add")
    public String addToCart(@RequestParam String item, HttpSession session) {
        List<String> cartItems = (List<String>) session.getAttribute("cartItems");
        cartItems.add(item);
        return  "redirect:/yiting/cart";
    }

    @PostMapping("/yiting/cart/clear")
    public String clearCart(@ModelAttribute("cartItems") List<String> cartItems) {
        cartItems.clear(); // 清空購物車項目列表
        return "redirect:/yiting/cart";
    }
}
*/

//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.util.CookieGenerator;
//import org.springframework.web.util.WebUtils;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Controller
//public class ShoppingCartController {
//
//    private final CookieGenerator cookieGenerator = new CookieGenerator();
//
//    @GetMapping("/yiting/cart")
//    public String viewCart(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
//        List<String> cartItems = getCartItemsFromCookies(request);
//        model.addAttribute("cartItems", cartItems);
//        return "back/yiting/test2";
//    }
//
//    @PostMapping("/yiting/cart/add")
//    public String addToCart(@RequestParam String item, HttpServletResponse response, HttpServletRequest request) throws IOException {
//        List<String> cartItems = getCartItemsFromCookies(request);
//        cartItems.add(item);
//        saveCartItemsToCookies(cartItems, response);
//        return "redirect:/yiting/cart";
//    }
//
//    @PostMapping("/yiting/cart/clear")
//    public String clearCart(HttpServletResponse response) {
//        cookieGenerator.setCookieName("cartItems");
//        cookieGenerator.removeCookie(response);
//        return "redirect:/yiting/cart";
//    }
//
//    private List<String> getCartItemsFromCookies(HttpServletRequest request) throws UnsupportedEncodingException {
//        Cookie cookie = WebUtils.getCookie(request, "cartItems");
//        if (cookie != null) {
//            String cookieValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
//            return new ArrayList<>(Arrays.asList(cookieValue.split(",")));
//        }
//        return new ArrayList<>();
//    }
//
//    private void saveCartItemsToCookies(List<String> cartItems, HttpServletResponse response) throws UnsupportedEncodingException {
//        String cookieValue = String.join(",", cartItems);
//        cookieGenerator.setCookieName("cartItems");
//        cookieGenerator.setCookieMaxAge(3600); // 設置 Cookie 存活時間為一小時
//        cookieGenerator.addCookie(response, URLEncoder.encode(cookieValue, "UTF-8"));
//    }
//}