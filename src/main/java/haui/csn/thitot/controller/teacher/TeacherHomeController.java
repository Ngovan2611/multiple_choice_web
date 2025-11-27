package haui.csn.thitot.controller.teacher;

import haui.csn.thitot.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherHomeController {

    @GetMapping("/teacher/home")
    public String teacherHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getRole().equals("teacher")) {
            return "redirect:/login";
        }

        model.addAttribute("teacher", user);
        return "teacher/index";
    }
}
