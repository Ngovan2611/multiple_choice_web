package haui.csn.thitot.controller.admin;

import haui.csn.thitot.entity.Question;
import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.service.AnswerService;
import haui.csn.thitot.service.QuestionService;
import haui.csn.thitot.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class QuestionManageController {
    @Autowired
    AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/admin/question_manage")
    public String questionManage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user != null && "admin".equals(user.getRole())) {

            List<Question> questions = questionService.getAllQuestions();
            List<Subject> subjects = subjectService.getAll();

            model.addAttribute("questions", questions);
            model.addAttribute("subjects", subjects);

            return "admin/question_manage";
        }

        session.invalidate();
        return "redirect:/login";
    }
    @PostMapping("/admin/question/save")
    public String saveQuestion(
            @RequestParam Integer subjectId,
            @RequestParam String questionText,
            @RequestParam String answerA,
            @RequestParam String answerB,
            @RequestParam String answerC,
            @RequestParam String answerD,
            @RequestParam String correct
    ) {
        Question q = questionService.createQuestion(subjectId, questionText, null);
        answerService.saveAnswer(q, answerA, answerB, answerC, answerD, correct);
        return "redirect:/admin/question_manage";
    }

    @PostMapping("/admin/question/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            questionService.delete(id);
            redirect.addFlashAttribute("success", "✅ Xóa thành công!");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/question_manage";
    }

    @PostMapping("/admin/question/update")
    public String updateQuestion(
            @RequestParam Integer questionId,
            @RequestParam String questionText,
            @RequestParam Integer subjectId,
            @RequestParam String answerA,
            @RequestParam String answerB,
            @RequestParam String answerC,
            @RequestParam String answerD,
            @RequestParam String correct
    ) {
        Question q = questionService.getQuestionsById(questionId);

        q.setQuestionText(questionText);

        Subject s = subjectService.getById(subjectId);
        q.setSubject(s);

        questionService.save(q);
        answerService.saveAnswer(q, answerA, answerB, answerC, answerD, correct);

        return "redirect:/admin/question_manage";
    }


}
