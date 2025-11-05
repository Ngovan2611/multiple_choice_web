package haui.csn.thitot.controller.user;


import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/subject")
    public String subject(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole().equals("admin") || user.getRole().equals("teacher")) {
            session.invalidate();
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        List<Subject> subjects = subjectService.getAll();
        model.addAttribute("subjects", subjects);
        return "/user/subject";
    }
}
