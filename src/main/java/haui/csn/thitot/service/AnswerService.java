package haui.csn.thitot.service;


import haui.csn.thitot.entity.Answer;
import haui.csn.thitot.entity.Question;
import haui.csn.thitot.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AnswerService {
    @Autowired
    private AnswerRepository answerRepository;

    // Lấy đáp án theo questionId
    public Answer getAnswerByQuestionId(Integer questionId) {
        return answerRepository.findByQuestionId(questionId).orElse(null);
    }

    // Lấy ký tự đáp án đúng (A/B/C/D)
    public String getCorrectAnswer(Integer questionId) {
        return answerRepository.findCorrectAnswerByQuestionId(questionId);
    }

    // Kiểm tra xem người dùng chọn đúng hay sai
    public boolean checkAnswer(Integer questionId, String userChoice) {
        Answer a = getAnswerByQuestionId(questionId);
        if (a == null) return false;

        return a.getIsCorrect().equalsIgnoreCase(userChoice);
    }
    public void saveAnswer(Question q,
                           String answerA,
                           String answerB,
                           String answerC,
                           String answerD,
                           String correct) {

        // Nếu question đã có answer → cập nhật
        Answer a = answerRepository.findByQuestionQuestionId(q.getQuestionId())
                .orElse(new Answer());

        a.setQuestion(q);
        a.setAnswerA(answerA);
        a.setAnswerB(answerB);
        a.setAnswerC(answerC);
        a.setAnswerD(answerD);
        a.setIsCorrect(correct);   // "A" / "B" / "C" / "D"

        answerRepository.save(a);
    }
}

