package haui.csn.thitot.controller.admin;


import haui.csn.thitot.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {
    @GetMapping("admin/home")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getRole().equals("admin")) {
            return "admin/index";
        }
        session.invalidate();
        return "redirect:/login";

    }
}
