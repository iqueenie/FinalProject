package six.pinhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import six.pinhong.model.ForbiddenWord;
import six.pinhong.service.ForbiddenWordService;

@Controller
public class ForbiddenWordsController {
	
	@Autowired
	private ForbiddenWordService forbiddenWordService;
	
	@GetMapping("/private/Product/ForbiddenWords")
	public String getAllForbiddenWords(Model m){
		
		List<ForbiddenWord> forbiddenWords = forbiddenWordService.getForbiddenWords();
		
		m.addAttribute("forbiddenWords",forbiddenWords);
		
		return "back/pinhong/GetAllForbiddenWord";
	}
	
	@PostMapping("/private/Product/addForbiddenWord")
	public String addForbiddenWord(@RequestParam String word, RedirectAttributes redirectAttributes) {
		if (forbiddenWordService.findByWord(word) != null) {
			redirectAttributes.addFlashAttribute("error", "此敏感詞已存在！請重新輸入");
			return "redirect:/private/Product/ForbiddenWords";
		}
	    forbiddenWordService.addForbiddenWord(word);
	    redirectAttributes.addFlashAttribute("message", "敏感词添加成功！");
	    return "redirect:/private/Product/ForbiddenWords";
	}

	@DeleteMapping("/Product/deleteForbiddenWord/{id}")
	@ResponseBody
	public String deleteForbiddenWord(@PathVariable Integer id) {
		System.out.println("Received delete request for id: " + id);
		forbiddenWordService.removeForbiddenWord(id);
	    return "success";
	}
}
	