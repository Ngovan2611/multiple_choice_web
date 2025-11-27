package haui.csn.thitot.repository;

import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Exam findById(int id);

    @Query(value = """
    SELECT e.*
    FROM exams e
    WHERE e.subject_id = :subjectId AND e.total_questions = :totalQuestions
    ORDER BY RAND() LIMIT 1
""", nativeQuery = true)
    Exam findRandomExamBySubjectAndQuestions(
            @Param("subjectId") Integer subjectId,
            @Param("totalQuestions") Integer totalQuestions
    );


    @Query("SELECT e FROM Exam e WHERE e.createdBy.role = 'teacher'")
    List<Exam> findAllByTeacherRole();
    @Query(value = "SELECT e.* FROM Exam e JOIN Result r ON e.examId = r.examId WHERE r.resultId = :resultId", nativeQuery = true)
    Exam findExamByResultId(@Param("resultId") Integer resultId);
    @Modifying
    @Query(value = "INSERT INTO exam_question (exam_id, question_id) VALUES (:examId, :questionId)", nativeQuery = true)
    void insert(@Param("examId") Integer examId, @Param("questionId") Integer questionId);

}
