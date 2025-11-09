package haui.csn.thitot.controller.teacher;

import haui.csn.thitot.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller("teacherProfileController")
public class ProfileController {

    @GetMapping("/teacher/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getRole().equals("teacher")) {
            return "redirect:/login";
        }

        model.addAttribute("teacher", user);
        model.addAttribute("editMode", false);
        return "teacher/profile";
    }

    @GetMapping("/teacher/profile/edit")
    public String editProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getRole().equals("teacher")) {
            return "redirect:/login";
        }

        model.addAttribute("teacher", user);
        model.addAttribute("editMode", true);
        return "teacher/profile";
    }

    @PostMapping("/teacher/profile/update")
    public String updateProfile(User form, HttpSession session) {
        User teacher = (User) session.getAttribute("user");

        teacher.setFull_name(form.getFull_name());
        teacher.setPhone(form.getPhone());
        teacher.setEmail(form.getEmail());

        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            teacher.setPassword(form.getPassword());
        }

        // Nếu dùng repository:
        // userRepository.save(teacher);

        session.setAttribute("user", teacher);

        return "redirect:/teacher/profile";
    }
}
