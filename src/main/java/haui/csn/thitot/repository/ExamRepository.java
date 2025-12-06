package haui.csn.thitot.repository;

import haui.csn.thitot.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

    @Query(value = """
        SELECT e.*
        FROM exams e
        WHERE e.subject_id = :subjectId 
          AND e.total_questions = :totalQuestions
        ORDER BY RAND() 
        LIMIT 1
    """, nativeQuery = true)
    Exam findRandomExamBySubjectAndQuestions(
            @Param("subjectId") Integer subjectId,
            @Param("totalQuestions") Integer totalQuestions
    );
    @Query(value = """
        SELECT e.*
        FROM exams e
        JOIN results r ON e.exam_id = r.exam_id
        WHERE r.result_id = :resultId
    """, nativeQuery = true)
    Exam findExamByResultId(@Param("resultId") Integer resultId);
    @Modifying
    @Query(value = "INSERT INTO exam_question (exam_id, question_id) VALUES (:examId, :questionId)", nativeQuery = true)
    void insert(@Param("examId") Integer examId, @Param("questionId") Integer questionId);
    List<Exam> findByCreatedBy_Id(Integer userId);
    List<Exam> findByCreatedBy_IdAndSubject_SubjectId(Integer teacherId, Integer subjectId);
    List<Exam> findByCreatedBy_IdAndExamNameContainingIgnoreCase(Integer teacherId, String keyword);
    List<Exam> findByCreatedBy_IdAndSubject_SubjectIdAndExamNameContainingIgnoreCase(Integer teacherId, Integer subjectId, String keyword);
    @Modifying
    @Transactional
    @Query("UPDATE Exam e SET e.totalQuestions = ?2 WHERE e.examId = ?1")
    void updateTotalQuestions(Integer examId, Integer totalCount);
}
