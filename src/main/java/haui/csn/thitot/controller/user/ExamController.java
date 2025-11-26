package haui.csn.thitot.controller.user;

import haui.csn.thitot.entity.*;
import haui.csn.thitot.service.AnswerService;
import haui.csn.thitot.service.ExamService;
import haui.csn.thitot.service.QuestionService;
import haui.csn.thitot.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private SubjectService subjectService;


    @GetMapping("/exam")
    public String exam(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole().equals("admin") || user.getRole().equals("teacher")) {
            session.invalidate();
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        List<Exam> exams = examService.getExamByCreateBy();
        model.addAttribute("exams", exams);

        return "/user/exam";
    }

    @GetMapping("/exam/{id}/take")
    public String takeExam(@PathVariable("id") Integer id, Model model) {
        Optional<Exam> examOpt = Optional.ofNullable(examService.getExamById(id));

        if (examOpt.isEmpty()) {
            return "redirect:/exam";
        }

        Exam exam = examOpt.get();
        model.addAttribute("exam", exam);

        List<Question> questions = questionService.getAllQuestionsByExam_Id(id);
        model.addAttribute("questions", questions);

        Map<Integer, Answer> answersMap = new HashMap<>();

        for (Question q : questions) {
            Answer ans = answerService.getAnswerByQuestionId(q.getQuestionId());
            if (ans != null) {
                answersMap.put(q.getQuestionId(), ans);
            }
        }

        model.addAttribute("answers", answersMap);

        return "/user/exam-take";
    }
    @GetMapping("/start")
    public String startExam(
            @RequestParam("subjectId") Integer subjectId,
            @RequestParam("difficulty") String difficulty,
            @RequestParam("count") int questionCount,
            Model model) {

        Exam exam = examService.getRandomExamBySubjectAndQuestions(subjectId, questionCount);

        if (exam == null) {
            model.addAttribute("message", "Không tìm thấy đề thi phù hợp!");
            return "/user/exam-notfound";
        }

        List<Question> questions = questionService.getAllQuestionsByExam_Id(exam.getExamId());

        if (questions == null || questions.isEmpty()) {
            model.addAttribute("message", "Đề thi chưa có câu hỏi!");
            return "/user/exam-notfound";
        }

        Map<Integer, Answer> answersMap = new HashMap<>();
        for (Question q : questions) {
            Answer ans = answerService.getAnswerByQuestionId(q.getQuestionId());
            if (ans != null) {
                answersMap.put(q.getQuestionId(), ans);
            }
        }

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("answers", answersMap);
        model.addAttribute("difficulty", difficulty);

        return "/user/exam-take";
    }
}
