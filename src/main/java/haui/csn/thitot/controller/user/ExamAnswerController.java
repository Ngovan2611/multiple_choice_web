package haui.csn.thitot.controller.user;

import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.ExamAnswer;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.service.ExamService;
import haui.csn.thitot.service.QuestionService;
import haui.csn.thitot.service.ResultService;
import haui.csn.thitot.service.ExamAnswerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ExamAnswerController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private ExamAnswerService examAnswerService;

    @Autowired
    private QuestionService questionService;
    @Autowired
    private ExamService examService;

    @GetMapping("/result/detail/{resultId}")
    public String viewResultDetail(@PathVariable Integer resultId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole().equals("admin") || user.getRole().equals("teacher")) {
            session.invalidate();
            return "redirect:/login";
        }

        var result = resultService.getResultById(resultId);
        List<ExamAnswer> examAnswers = examAnswerService.getExamAnswerDetails(resultId);
        Exam exam = examService.getExamById(result.getExam().getExamId());

        Map<Integer, String> selectedAnswers = examAnswers.stream()
                .collect(Collectors.toMap(
                        ea -> ea.getQuestion().getQuestionId(),
                        ea -> ea.getSelectedAnswerId() != null ? String.valueOf(ea.getSelectedAnswerId()) : "",
                        (a, b) -> a
                ));
        model.addAttribute("selectedAnswers", selectedAnswers);


        model.addAttribute("exam", exam);
        model.addAttribute("user", user);
        model.addAttribute("result", result);
        model.addAttribute("selectedAnswers", selectedAnswers);
        model.addAttribute("questions", exam.getQuestions());
        return "/user/history_detail";
    }




}
