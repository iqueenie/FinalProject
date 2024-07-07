package six.all;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PageController {
	@GetMapping("/private/back")
	public String backIndex() {
		return "/back/Index";
	}
}
