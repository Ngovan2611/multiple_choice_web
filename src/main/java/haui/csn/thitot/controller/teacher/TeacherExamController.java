package haui.csn.thitot.controller.teacher;

import haui.csn.thitot.entity.*;
import haui.csn.thitot.repository.QuestionRepository;
import haui.csn.thitot.repository.ResultRepository;
import haui.csn.thitot.service.ExamService;
import haui.csn.thitot.service.QuestionService;
import haui.csn.thitot.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/teacher/exam")
public class TeacherExamController {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ExamService examService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ResultRepository resultRepository;
    // HIỂN THỊ DANH SÁCH ĐỀ THI
// --------------------------------------------------
    @GetMapping
    public String listExams(@RequestParam(required = false) Integer subjectId,
                             @RequestParam(required = false) String keyword,
                            HttpSession session,
                            Model model) {
        User teacher = (User) session.getAttribute("user");
        if (teacher == null || !"teacher".equals(teacher.getRole())) {
            return "redirect:/login";
        }

        // Lấy đề thi chỉ của giáo viên hiện tại
        List<Exam> exams = examService.getExamsByTeacher(teacher.getId());
        List<Subject> subjects = subjectService.getAll();
        model.addAttribute("subjects", subjects);
        model.addAttribute("selectedSubjectId", subjectId);
        model.addAttribute("keyword", keyword);
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (subjectId != null) {
            if (hasKeyword) {
                // Trường hợp 1: Lọc theo Môn học VÀ Từ khóa
                exams = examService.searchBySubjectAndKeyword(teacher.getId(), subjectId, keyword.trim());
            } else {
                // Trường hợp 2: Chỉ Lọc theo Môn học
                exams = examService.getExamsByTeacherAndSubject(teacher.getId(), subjectId);
            }
        } else if (hasKeyword) {
            // Trường hợp 3: Chỉ Tìm kiếm theo Từ khóa (Trên toàn bộ đề thi của giáo viên)
            exams = examService.searchByTeacherAndKeyword(teacher.getId(), keyword.trim());
        } else {
            // Trường hợp 4: Không có điều kiện nào (Hiển thị tất cả đề thi của giáo viên)
            exams = examService.getExamsByTeacher(teacher.getId());
        }
        model.addAttribute("exams", exams);
        return "teacher/exam";
    }
    @PostMapping("/save")
    public String saveExam(@RequestParam String examName,
                           @RequestParam Integer subjectId,
                           @RequestParam Integer duration,
                           @RequestParam Integer totalQuestions,
                           @RequestParam(required = false) String description,
                           HttpSession session) {
        User teacher = (User) session.getAttribute("user");
        if (teacher == null) {
            return "redirect:/login";
        }
        Exam newExam = examService.createExam(examName, duration, totalQuestions, subjectId, teacher, description);
        return "redirect:/teacher/exam/add_questions/" + newExam.getExamId();
    }
// HIỂN THỊ TRANG CHỌN CÂU HỎI
// --------------------------------------------------
    @GetMapping("/add_questions/{examId}")
    public String showAddQuestionsForm(@PathVariable Integer examId, Model model) {

        Exam exam = examService.findById(examId);

        if (exam == null) {
            return "redirect:/teacher/exam";
        }

        // Lấy tất cả câu hỏi thuộc cùng môn học (Question Bank)
        List<Question> availableQuestions = questionRepository.findAvailableQuestions(exam.getSubject().getSubjectId(), examId);
        // 💡 BỔ SUNG: Lấy ID các câu hỏi đã được gán cho đề thi này
        Set<Integer> selectedQuestionIds = examService.getSelectedQuestionIds(examId);
        if (selectedQuestionIds == null) {
            selectedQuestionIds = java.util.Collections.emptySet();
        }

        model.addAttribute("exam", exam);
        model.addAttribute("questions", availableQuestions);
        model.addAttribute("selectedQuestionIds", selectedQuestionIds);

        return "teacher/exam_add_questions";
    }
    // XỬ LÝ LƯU CÂU HỎI ĐÃ CHỌN
// --------------------------------------------------
    @PostMapping("/save_questions/{examId}")
    public String saveQuestions(@PathVariable Integer examId,
                                // Đảm bảo tên tham số khớp với tên input[name="questionIds"]
                                @RequestParam(name = "questionIds", required = false) List<Integer> questionIds) {

        // Gọi Service với tham số đã sửa tên
        examService.updateExamQuestions(examId, questionIds);

        // Chuyển hướng về trang chọn câu hỏi để giáo viên kiểm tra lại số lượng đã chọn.
        return "redirect:/teacher/exam/add_questions/" + examId;
    }
    // CẬP NHẬT ĐỀ THI (EDIT - LẤY DỮ LIỆU qua AJAX)
// --------------------------------------------------
    @GetMapping("/api/exam/{id}")
    @ResponseBody
    public Exam getExamById(@PathVariable Integer id) {
        return examService.findById(id);
    }
    // CẬP NHẬT ĐỀ THI (POST - LƯU)
// --------------------------------------------------
    @PostMapping("/update/{id}")
    public String updateExam(@PathVariable Integer id,
                             @RequestParam String examName,
                             @RequestParam Integer totalQuestions,
                             @RequestParam Integer duration,
                             @RequestParam(required = false) String description) {

        examService.updateExamDetails(id, examName, duration,totalQuestions, description);

        return "redirect:/teacher/exam?status=success";
    }

    // XÓA ĐỀ THI
    @GetMapping("/delete/{id}")
    public String deleteExam(@PathVariable Integer id) {
        try {
            examService.deleteExam(id);

            return "redirect:/teacher/exam?status=success";

        } catch (Exception e) {
            System.err.println("LỖI SEVER XÓA ĐỀ THI: " + e.getMessage());

            return "redirect:/teacher/exam?status=error";
        }
    }
    // HIỂN THỊ TRANG BÁO CÁO (THỐNG KÊ)
// --------------------------------------------------
    @GetMapping("/report/{id}")
    public String showExamReport(@PathVariable Integer id, Model model) {
        Exam exam = examService.findById(id);
        if (exam == null) {
            return "redirect:/teacher/exam";
        }
        Long totalAttempts = resultRepository.countByExam_ExamId(id);
        Double avgScore = resultRepository.findAverageScoreByExamId(id);
        Double maxScore = resultRepository.findMaxScoreByExamId(id);
        model.addAttribute("exam", exam);
        List<Result> detailedResults = resultRepository.findByExam_ExamId(id);
        model.addAttribute("exam", exam);
        model.addAttribute("totalAttempts", totalAttempts);
        model.addAttribute("avgScore", avgScore);
        model.addAttribute("maxScore", maxScore);
        model.addAttribute("detailedResults", detailedResults);

        return "teacher/exam_report";
    }

    @GetMapping("/report/{id}/export")
    public ResponseEntity<InputStreamResource> exportReport(@PathVariable Integer id) {

        List<Result> results = resultRepository.findByExam_ExamId(id);
        Exam exam = examService.findById(id);
        String examName = (exam != null) ? exam.getExamName() : "Unknown_Exam";

        // 2. Tạo file Excel
        ByteArrayInputStream in = examService.exportToExcel(results, examName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=ket_qua_thi_" + id + ".xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
    @GetMapping("/cancel/{id}")
    public String cancelExam(@PathVariable Integer id) {
        try {
            // Kiểm tra logic: Nếu đề thi chưa có câu hỏi nào (đề thi rác) -> Xóa luôn
            if (examService.isExamEmpty(id)) {
                examService.deleteExam(id);
                return "redirect:/teacher/exam?status=cancelled"; // Thông báo đã hủy
            }

            // Nếu đề thi đã có câu hỏi (đang sửa) -> Chỉ quay về danh sách, không xóa
            return "redirect:/teacher/exam";

        } catch (Exception e) {
            System.err.println("Lỗi khi hủy đề thi: " + e.getMessage());
            return "redirect:/teacher/exam";
        }
    }
}
