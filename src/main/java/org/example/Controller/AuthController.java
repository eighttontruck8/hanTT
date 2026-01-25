package org.example.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.DTO.LoginRequest;
import org.example.DTO.SignupRequest;
import org.example.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.example.Service.MemberService;

@Controller
public class AuthController {

    public static final String SESSION_KEY = "LOGIN_MEMBER_ID";
    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("form", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("form") SignupRequest form, Model model) {
        try {
            memberService.signup(form.getEmail(), form.getPassword(), form.getNickname());
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/login")
    public String loginForm(
            @CookieValue(value = "REMEMBER_EMAIL", required = false) String rememberedEmail,
            Model model
    ) {
        LoginRequest form = new LoginRequest();
        if (rememberedEmail != null) {
            form.setEmail(rememberedEmail);
            form.setRememberEmail(true);
        }
        model.addAttribute("form", form);
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("form") LoginRequest form,
            HttpSession session,
            HttpServletResponse response,
            Model model
    ) {
        try {
            Member member = memberService.login(form.getEmail(), form.getPassword());

            session.setAttribute(SESSION_KEY, member.getId());

            if (form.isRememberEmail()) {
                Cookie c = new Cookie("REMEMBER_EMAIL", member.getEmail());
                c.setPath("/");
                c.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(c);
            } else {
                Cookie c = new Cookie("REMEMBER_EMAIL", "");
                c.setPath("/");
                c.setMaxAge(0);
                response.addCookie(c);
            }

            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}