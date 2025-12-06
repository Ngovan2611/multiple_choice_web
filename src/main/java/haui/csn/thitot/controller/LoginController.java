package haui.csn.thitot.controller;

import haui.csn.thitot.entity.User;
import haui.csn.thitot.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {

        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            model.addAttribute("error", "Tài khoản không tồn tại!");
            return "user/login";
        }

        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Sai mật khẩu!");
            return "user/login";
        }

        session.setAttribute("user", user);

        switch (user.getRole()) {
            case "admin" -> {
                return "redirect:/admin/home";
            }
            case "teacher" -> {
                return "redirect:/teacher/home";
            }
            default -> {
                return "redirect:/home";
            }
        }

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


}
