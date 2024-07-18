package six.liang.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import six.liang.model.Award;
import six.liang.model.AwardRepository;

@Controller
public class AwardController {

    @Autowired
    private AwardRepository awardRepository;

    // 查全部獎項
    @GetMapping("/private/awards")
    public String getAllAwards(Model model) {
        List<Award> awards = awardRepository.findAll();
        model.addAttribute("awards", awards);
        return "back/liang/GetAllAwards";
    }

    // 新增獎項表單
    @GetMapping("/private/awards/create")
    public String showCreateForm() {
        return "back/liang/CreateAward";
    }

    // 新增獎項
    @PostMapping("/private/awards/create")
    public String createAward(@RequestParam("name") String name,
                              @RequestParam("points") int points,
                              @RequestParam("probability") double probability) {

        Award award = new Award();
        award.setName(name);
        award.setPoints(points);
        award.setProbability(probability);

        awardRepository.save(award);
        return "redirect:/private/awards";
    }

    // 刪除獎項
    @PostMapping("/private/awards/delete")
    public String deleteAward(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        if (awardRepository.existsById(id)) {
            awardRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "刪除成功！");
        } else {
            redirectAttributes.addFlashAttribute("message", "刪除失敗！");
        }
        return "redirect:/private/awards";
    }

    // 更新獎項表單
    @GetMapping("/private/awards/update")
    public String showUpdateForm(@RequestParam("id") Long id, Model model) {
        Optional<Award> optionalAward = awardRepository.findById(id);
        if (optionalAward.isPresent()) {
            model.addAttribute("award", optionalAward.get());
            return "back/liang/UpdateAward";
        } else {
            return "redirect:/awards";
        }
    }

    // 更新獎項
    @PostMapping("/private/awards/update")
    public String updateAward(@RequestParam("id") Long id,
                              @RequestParam("name") String name,
                              @RequestParam("points") int points,
                              @RequestParam("probability") double probability) {

        Optional<Award> optionalAward = awardRepository.findById(id);
        if (optionalAward.isPresent()) {
            Award award = optionalAward.get();
            award.setName(name);
            award.setPoints(points);
            award.setProbability(probability);
            awardRepository.save(award);
        }

        return "redirect:/private/awards";
    }
    
    
}
