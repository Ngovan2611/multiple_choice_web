package haui.csn.thitot.controller.user;

import haui.csn.thitot.entity.User;
import haui.csn.thitot.repository.UserRepository;
import haui.csn.thitot.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/home"})
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole().equals("admin") || user.getRole().equals("teacher")) {
            session.invalidate();
            return "user/index";
        }
        model.addAttribute("user", user);
        return "user/index";
    }
}
