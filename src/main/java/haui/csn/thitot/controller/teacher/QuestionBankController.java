package haui.csn.thitot.controller.teacher;


import haui.csn.thitot.entity.*;
import haui.csn.thitot.service.AnswerService;
import haui.csn.thitot.service.QuestionService;
import haui.csn.thitot.service.SubjectService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/teacher/question_bank")
public class QuestionBankController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;

    // HIỂN THỊ TRANG NGÂN HÀNG CÂU HỎI
    @GetMapping
    public String listQuestions(@RequestParam(required = false) Integer subjectId,
                                @RequestParam(required = false) String keyword,
                                HttpSession session,
                                Model model) {

        User teacher = (User) session.getAttribute("user");
        if (teacher == null || !"teacher".equals(teacher.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("subjects", subjectService.getAll());
        model.addAttribute("selectedSubject", subjectId);
        model.addAttribute("keyword", keyword);

        List<Question> questions = null;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (subjectId != null) {
            if (hasKeyword) {
                questions = questionService.searchQuestionsBySubjectAndKeyword(subjectId, keyword.trim());
            } else {
                questions = questionService.getQuestionsBySubject(subjectId);
            }
        } else if (hasKeyword) {
            questions = questionService.search(keyword.trim());
        } else {
            questions = questionService.getAllQuestions();
        }

        model.addAttribute("questions", questions);

        return "teacher/question_bank";
    }

    // TẠO CÂU HỎI (POPUP)
    @PostMapping("/create")
    public String createQuestion(@RequestParam Integer subjectId,
                                 @RequestParam String questionText,
                                 @RequestParam String answerA,
                                 @RequestParam String answerB,
                                 @RequestParam String answerC,
                                 @RequestParam String answerD,
                                 @RequestParam String correct,
                                 @RequestParam(required = false) String imageUrl) {

        Question q = questionService.createQuestion(subjectId, questionText, imageUrl);

        answerService.saveAnswer(q, answerA, answerB, answerC, answerD, correct);

        return "redirect:/teacher/question_bank?subjectId=" + subjectId;
    }


    // LẤY DỮ LIỆU 1 CÂU HỎI (AJAX - DETAIL + EDIT)
    @GetMapping("/api/question/{id}")
    @ResponseBody
    public Question getQuestionById(@PathVariable Integer id) {
        return questionService.getQuestionsById(id);
    }


    // CẬP NHẬT CÂU HỎI (POPUP)
    @PostMapping("/update")
    public String updateQuestion(@RequestParam Integer questionId,
                                 @RequestParam String questionText,
                                 @RequestParam(required = false) String imageUrl,
                                 @RequestParam String answerA,
                                 @RequestParam String answerB,
                                 @RequestParam String answerC,
                                 @RequestParam String answerD,
                                 @RequestParam String correct) {

        Question q = questionService.getQuestionsById(questionId);
        Integer subjectId = q.getSubject().getSubjectId();

        q.setQuestionText(questionText);
        q.setImageUrl(imageUrl);

        questionService.save(q);
        answerService.saveAnswer(q, answerA, answerB, answerC, answerD, correct);
        return "redirect:/teacher/question_bank?subjectId=" + subjectId;
    }


    // ===================== XÓA CÂU HỎI =====================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        Question q = questionService.getQuestionById(id);

        if (q == null) {
            return "redirect:/teacher/question_bank";
        }

        Integer subjectId = q.getSubject().getSubjectId();

        try {
            questionService.delete(id);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa câu hỏi: " + e.getMessage());
        }

        return "redirect:/teacher/question_bank?subjectId=" + subjectId;
    }
}
