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
public class StudentManageController {

    @Autowired
    private UserService userService;

    // 📘 Danh sách học sinh
    @GetMapping("/admin/student_manage")
    public String studentManage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && "admin".equals(user.getRole())) {
            List<User> students = userService.getUsersByRole("student");
            model.addAttribute("students", students);
            return "admin/student_manage";
        }
        session.invalidate();
        return "redirect:/login";
    }

    // 🟢 Thêm hoặc sửa học sinh
    @PostMapping("/admin/student/save")
    public String saveOrUpdateStudent(
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
            // Cập nhật học sinh
            User existing = userService.getUserById(id);
            if (existing == null) {
                model.addAttribute("errorMessage", "Không tìm thấy học sinh cần sửa.");
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
            // Thêm mới học sinh
            if (userService.existsUserByEmail(email)) {
                model.addAttribute("errorMessage", "Email này đã tồn tại. Vui lòng nhập email khác.");
            } else if (userService.existsUserByPhone(phone)) {
                model.addAttribute("errorMessage", "Số điện thoại này đã tồn tại. Vui lòng nhập số khác.");
            } else {
                User user = new User();
                user.setFull_name(name);
                user.setUsername(username);
                user.setPassword(password);
                user.setRole("student");
                user.setPhone(phone);
                user.setEmail(email);
                user.setClassName(className);
                userService.saveUser(user);
            }
        }

        List<User> students = userService.getUsersByRole("student");
        model.addAttribute("students", students);
        return "admin/student_manage";
    }

    // 🔴 Xóa học sinh
    @ResponseBody
    @DeleteMapping("/admin/student/delete/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        try {
            userService.deleteUserById(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
}
