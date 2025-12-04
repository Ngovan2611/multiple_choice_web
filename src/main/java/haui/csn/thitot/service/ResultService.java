package haui.csn.thitot.service;

import haui.csn.thitot.entity.*;
import haui.csn.thitot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ResultService {
    @Autowired private QuestionService questionService;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private ExamService examService;
    @Autowired private ResultRepository resultRepository;
    @Autowired private ExamAnswerRepository examAnswerRepository;

    public Result gradeExamAndSave(User user, Integer examId, Map<Integer, String> userAnswers) {
        List<Question> questions = questionService.getAllQuestionsByExam_Id(examId);
        int correct = 0, incorrect = 0;
        Result result = new Result();
        result.setUser(user);
        result.setExam(examService.getExamById(examId));
        result.setStartTime(LocalDateTime.now().minusMinutes(45));
        result.setEndTime(LocalDateTime.now());
        result.setStatus(Result.Status.completed);

        List<ExamAnswer> examAnswers = new ArrayList<>();
        for (Question q : questions) {
            String userChoice = userAnswers.get(q.getQuestionId());
            if (userChoice == null) continue;

            String correctAnswer = answerRepository.findCorrectAnswerByQuestionId(q.getQuestionId());

            boolean isCorrect = userChoice.equalsIgnoreCase(correctAnswer);
            if (isCorrect) correct++;
            else incorrect++;

            ExamAnswer ea = new ExamAnswer();
            ea.setQuestion(q);
            ea.setResult(result);
            ea.setSelectedAnswerId(convertOptionToNumber(userChoice));
            ea.setIsCorrect(correctAnswer);
            ea.setAnsweredAt(LocalDateTime.now());
            examAnswers.add(ea);
        }
        double score = (double) correct / questions.size() * 10.0;
        result.setScore(score);
        result.setCorrectCount(correct);
        result.setIncorrectCount(incorrect);
        resultRepository.save(result);
        for (ExamAnswer ea : examAnswers) {
            examAnswerRepository.save(ea);
        }
        return result;
    }
    private Integer convertOptionToNumber(String option) {
        if (option == null) return null;
        return switch (option.toUpperCase()) {
            case "A" -> 1;
            case "B" -> 2;
            case "C" -> 3;
            case "D" -> 4;
            default -> null;
        };
    }
    public List<Result> getResultsByUser(User user) {
        return resultRepository.findByUser(user);
    }
    public Result getResultById(Integer id) {
        return resultRepository.findByResultId(id);
    }
}
