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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExamManageController {

    @Autowired
    private ExamService examService;

    @Autowired
    private UserService userService;

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
            return "admin/exam_manage";
        }
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/admin/questions/by-subject/{subjectId}")
    @ResponseBody
    public List<Map<String, Object>> getQuestionsBySubject(@PathVariable Integer subjectId) {
        List<Question> questions = questionService.getAllBySubject_Id(subjectId);

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
            @RequestParam Integer subjectId,   // ✅ BẮT BUỘC PHẢI CÓ
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam int questionCount,
            @RequestParam int duration,
            @RequestParam List<Integer> questionIds,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");

        Subject subject = subjectService.getById(subjectId);
        if (subject == null) {
            throw new RuntimeException("Không tìm thấy môn học!");
        }

        Exam exam = new Exam();
        exam.setExamName(title);
        exam.setDescription(description);
        exam.setTotalQuestions(questionCount);
        exam.setDuration(duration);
        exam.setCreatedBy(user);

        // ✅ DÒNG QUAN TRỌNG NHẤT → SỬA LỖI 500
        exam.setSubject(subject);

        examService.save(exam);

        examService.addQuestionsToExam(exam.getExamId(), questionIds);

        return "redirect:/admin/exam_manage";
    }

}

