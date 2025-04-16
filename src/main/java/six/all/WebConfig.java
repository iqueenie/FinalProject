package six.all;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	
	 @Bean
	    public AuthenticationInterceptor authenticationInterceptor() {
	        return new AuthenticationInterceptor();
	    }
    
    
	 @Override
	    public void addInterceptors(InterceptorRegistry registry) {      //這裡攔截的是/private底下的所有路徑            //這裡除外路徑 有特別的功能 可以寫在這裡 
	        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/private/**").excludePathPatterns("/public/**","/public/login");
	        	
	 }
	 
}