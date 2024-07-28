package six.hsiao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.hsiao.model.Message;
import six.hsiao.service.MessageService;

import java.util.List;


@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

   
    
    // 會員發送訊息給管理員
    @PostMapping("/public/sendMessageToAdmin")
    public String sendMessageToAdmin(
            @RequestParam("messageContent") String messageContent,
            @SessionAttribute("loggedInMember") MembersBean loggedInMember,
            RedirectAttributes redirectAttributes) {

        messageService.sendMessageFromMember(loggedInMember.getMemberId(), messageContent);
        redirectAttributes.addFlashAttribute("message", "訊息已發送給管理員");
        return "redirect:/public/MemberInbox"; 
    }

    // 查看會員訊息
    @GetMapping("/memberProfile")
    public String getMemberProfile(@SessionAttribute("loggedInMember") MembersBean loggedInMember, Model model) {
        List<Message> messages = messageService.getMessagesForMember(loggedInMember.getMemberId());
        model.addAttribute("messages", messages);
        return "redirect:/front/memberProfileMain"; 
    }
   
    
    // 管理員發送訊息給單個會員
    @PostMapping("/sendToMember")
    public String sendMessageToMember(
            @RequestParam("receiverMemberId") Long receiverMemberId,
            @RequestParam("messageContent") String messageContent,
            RedirectAttributes redirectAttributes) {

        messageService.sendMessageFromAdmin(receiverMemberId, messageContent);
        redirectAttributes.addFlashAttribute("message", "訊息已發送給會員");
        return "redirect:/private/AdminMessagesMain"; 
    }

    // 管理員發送訊息給所有會員
    @PostMapping("/sendToAllMembers")
    public String sendMessageToAllMembers(
            @RequestParam("messageContent") String messageContent,
            RedirectAttributes redirectAttributes) {

        messageService.sendMessageFromAdminToAllMembers(messageContent);
        redirectAttributes.addFlashAttribute("message", "訊息已發送給所有會員");
        return "redirect:/private/AdminMessagesMain"; 
    }
    
    //拿到會員所有的訊息
    @GetMapping("/private/AdminMessagesMain")
    public String adminMessagesMain(Model model) {
       
       
        return "back/hsiao/AdminMessages";
    }	
    
    
    
    @GetMapping("/public/MemberInbox")
    public String viewMemberInbox(HttpSession session, Model model) {
        
        Integer memberId = ((MembersBean) session.getAttribute("loggedInMember")).getMemberId();
        
       
        List<Message> messages = messageService.getMessagesForMember(memberId);
        
       
        model.addAttribute("messages", messages);
        
      
        return "front/hsiao/MemberInbox"; 
    }
    
    
}


    
    
    


