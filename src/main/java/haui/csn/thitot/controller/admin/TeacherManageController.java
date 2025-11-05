package haui.csn.thitot.controller.admin;

import haui.csn.thitot.entity.User;
import haui.csn.thitot.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class TeacherManageController {

    @Autowired
    UserService userService;

    @GetMapping("admin/teacher_manage")
    public String teacherManage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && "admin".equals(user.getRole())) {
            List<User> teachers = userService.getUsersByRole("teacher");
            model.addAttribute("teachers", teachers);
            return "admin/teacher_manage";
        }
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/admin/teacher/save")
    public String saveOrUpdateTeacher(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam(value = "className", required = false) String className,
            @RequestParam("username") String username,
            @RequestParam(value = "password", required = false) String password,
            Model model
    ) {
        if (id != null) {
            User existing = userService.getUserById(id);
            if (existing == null) {
                model.addAttribute("errorMessage", "Không tìm thấy giáo viên cần sửa.");
            } else {
                existing.setFull_name(name);
                existing.setPhone(phone);
                existing.setEmail(email);
                existing.setClassName(className);
                existing.setUsername(username);
                if (password != null && !password.trim().isEmpty()) {
                    existing.setPassword(password);
                }
                userService.saveUser(existing);
            }
        } else {
            if (userService.existsUserByEmail(email)) {
                model.addAttribute("errorMessage", "Email này đã tồn tại. Vui lòng nhập email khác.");
            } else if (userService.existsUserByPhone(phone)) {
                model.addAttribute("errorMessage", "Số điện thoại này đã tồn tại. Vui lòng nhập số khác.");
            } else {
                User user = new User();
                user.setFull_name(name);
                user.setUsername(username);
                user.setPassword(password);
                user.setRole("teacher");
                user.setPhone(phone);
                user.setEmail(email);
                user.setClassName(className);
                userService.saveUser(user);
            }
        }

        List<User> teachers = userService.getUsersByRole("teacher");
        model.addAttribute("teachers", teachers);
        return "admin/teacher_manage";
    }

    @ResponseBody
    @DeleteMapping("/admin/teacher/delete/{id}")
    public String deleteTeacher(@PathVariable Integer id) {
        try {
            userService.deleteUserById(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

}
