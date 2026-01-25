package org.example.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.Service.MemberService;
import org.example.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MemberService memberService;

    public HomeController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute(AuthController.SESSION_KEY);

        if (memberId == null) {
            return "redirect:/login";
        }

        Member member = memberService.getMember(memberId);
        model.addAttribute("member", member);
        return "home";
    }
}
