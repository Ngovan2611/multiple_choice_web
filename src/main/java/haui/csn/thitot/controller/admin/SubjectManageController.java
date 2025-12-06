package haui.csn.thitot.controller.admin;

import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class SubjectManageController {

    @Autowired
    SubjectService subjectService;

    // ✅ HIỂN THỊ DANH SÁCH
    @GetMapping("/admin/subject_manage")
    public String adminSubjectManage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user != null && "admin".equals(user.getRole())) {
            List<Subject> subjects = subjectService.getAll();
            model.addAttribute("subjects", subjects);
            return "admin/subject_manage";
        }

        session.invalidate();
        return "redirect:/login";
    }

    // ✅ THÊM / SỬA + UPLOAD ẢNH
    @PostMapping("/admin/subject/save")
    public String saveSubject(
            @RequestParam(required = false) Integer id,
            @RequestParam("subjectName") String subjectName,
            @RequestParam("description") String description,
            @RequestParam("imageFile") MultipartFile imageFile
    ) {
        try {
            Subject subject;

            if (id != null) {
                subject = subjectService.getById(id);
            } else {
                subject = new Subject();
            }

            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                String uploadDir = "src/main/resources/static/image";

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(imageFile.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                subject.setImageUrl("/image/" + fileName);
            }

            subject.setSubjectName(subjectName);
            subject.setDescription(description);

            subjectService.save(subject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/subject_manage";
    }

    // ✅ XÓA MÔN HỌC (AJAX)
    @DeleteMapping("/admin/subject/delete/{id}")
    @ResponseBody
    public String deleteSubject(@PathVariable Integer id) {
        try {
            subjectService.deleteById(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
