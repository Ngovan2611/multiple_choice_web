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
        return "user/login"; // => templates/user/login.html
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

        // ✅ Lưu thông tin user vào session
        session.setAttribute("user", user);

        // ✅ Phân quyền điều hướng
        switch (user.getRole()) {
            case "admin":
                return "admin/index"; // => AdminController
            case "teacher":
                return "teacher/index";
            case "user":
            default:
                return "user/index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping
    public String loginn(Model model) {
        return "user/login";
    }
}
