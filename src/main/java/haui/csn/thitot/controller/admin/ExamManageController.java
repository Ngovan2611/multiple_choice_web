package haui.csn.thitot.controller.admin;


import haui.csn.thitot.entity.Answer;
import haui.csn.thitot.entity.Question;
import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.service.QuestionService;
import haui.csn.thitot.service.SubjectService;
import org.springframework.ui.Model;
import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.service.ExamService;
import haui.csn.thitot.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class ExamManageController {

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping("admin/exam_manage")
    public String examManage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && "admin".equals(user.getRole())) {
            List<Exam> exams = examService.getAll();
            model.addAttribute("exams", exams);
            model.addAttribute("subjects", subjectService.getAll());
            return "admin/exam_manage";
        }
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/admin/questions/by-subject/{subjectId}")
    @ResponseBody
    public List<Map<String, Object>> getQuestionsBySubject(@PathVariable Integer subjectId) {
        List<Question> questions = questionService.getFreeQuestionsBySubject(subjectId);

        return questions.stream().map(q -> {
            Map<String, Object> data = new HashMap<>();
            data.put("questionId", q.getQuestionId());
            data.put("questionText", q.getQuestionText());

            Answer a = q.getAnswer();
            Map<String, Object> answers = new LinkedHashMap<>();
            answers.put("A", a.getAnswerA());
            answers.put("B", a.getAnswerB());
            answers.put("C", a.getAnswerC());
            answers.put("D", a.getAnswerD());
            answers.put("correct", a.getIsCorrect());

            data.put("answers", answers);
            return data;
        }).toList();
    }

    @PostMapping("/admin/exams/save-full")
    public String saveExam(
            @RequestParam(required = false) Integer examId, // Thêm ID để biết là Sửa hay Thêm
            @RequestParam Integer subjectId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam int questionCount,
            @RequestParam int duration,
            @RequestParam(required = false) List<Integer> questionIds, // Có thể rỗng nếu xóa hết câu hỏi
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        Subject subject = subjectService.getById(subjectId);

        Exam exam;
        if (examId != null) {
            // --- TRƯỜNG HỢP SỬA ---
            exam = examService.getExamById(examId); // Lấy đề cũ
        } else {
            // --- TRƯỜNG HỢP THÊM MỚI ---
            exam = new Exam();
            exam.setCreatedBy(user);
        }

        // Cập nhật thông tin chung
        exam.setExamName(title);
        exam.setDescription(description);
        exam.setTotalQuestions(questionCount);
        exam.setDuration(duration);
        exam.setSubject(subject);

        examService.save(exam);

        if (questionIds == null) questionIds = new ArrayList<>();
        examService.updateExamQuestions(exam.getExamId(), questionIds);

        return "redirect:/admin/exam_manage";
    }

    @GetMapping("/admin/exams/delete/{id}")
    @ResponseBody
    public String deleteExam(@PathVariable Integer id) {
        try {
            examService.deleteExam(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/admin/exams/{examId}/question-ids")
    @ResponseBody
    public List<Integer> getExamQuestionIds(@PathVariable Integer examId) {
        return questionService.getQuestionIdsByExamId(examId);
    }
}

