package haui.csn.thitot.repository;

import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Integer> {

//    @Query("""
//    SELECT q.questionText, q.imageUrl,
//           a.answerA, a.answerB, a.answerC, a.answerD,
//           a.isCorrect, ea.selectedOption, ea.isCorrect
//    FROM ExamAnswer ea
//    JOIN ea.question q
//    JOIN Answer a ON a.question = q
//    WHERE ea.result.resultId = :resultId
//""")
//    List<Object[]> findDetailsByResultId(@Param("resultId") Integer resultId);

    @Query("""
        SELECT ea FROM ExamAnswer ea
        JOIN FETCH ea.question q
        WHERE ea.result.resultId = :resultId
    """)
    List<ExamAnswer> findByResultId(@Param("resultId") Integer resultId);
    boolean existsByQuestion_QuestionId(Integer questionId);
}

