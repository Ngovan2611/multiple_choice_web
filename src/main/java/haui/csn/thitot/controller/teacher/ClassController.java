package haui.csn.thitot.controller.teacher;

import haui.csn.thitot.entity.User;
import haui.csn.thitot.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/teacher/class")
public class ClassController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String listStudents(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               HttpSession session,
                               Model model) {

        User teacher = (User) session.getAttribute("user");
        if (teacher == null || !"teacher".equals(teacher.getRole())) {
            return "redirect:/login";
        }

        String myClass = teacher.getClassName();

        if (myClass == null || myClass.isEmpty()) {
            model.addAttribute("message", "Bạn chưa được phân công chủ nhiệm lớp nào.");
            model.addAttribute("students", java.util.Collections.emptyList());
        } else {
            List<User> students = userService.getStudentsByTeacherClass(myClass, keyword, sort);
            model.addAttribute("students", students);
            model.addAttribute("totalStudents", students.size());
            model.addAttribute("currentClass", myClass);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return "teacher/class";
    }
}
