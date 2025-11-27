package haui.csn.thitot.repository;

import haui.csn.thitot.entity.Answer;
import haui.csn.thitot.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    // Lấy đáp án theo đối tượng Question
    Optional<Answer> findByQuestion(Question question);

    // Hoặc nếu bạn chỉ có questionId
    @Query("SELECT a FROM Answer a WHERE a.question.questionId = :questionId")
    Optional<Answer> findByQuestionId(Integer questionId);

    // Nếu chỉ cần lấy ký tự đáp án đúng (A/B/C/D)
    @Query("SELECT a.isCorrect FROM Answer a WHERE a.question.questionId = :questionId")
    String findCorrectAnswerByQuestionId(@Param("questionId") Integer questionId);

    Optional<Answer> findByQuestionQuestionId(Integer questionId);

}
