package haui.csn.thitot.repository;


import haui.csn.thitot.entity.Question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findByExam_ExamId(Integer examId);

    @Modifying
    @Transactional
    @Query("UPDATE Question q SET q.exam = NULL WHERE q.exam.examId = ?1")
    void removeQuestionsFromExam(Integer examId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE questions SET exam_id = ?1 WHERE question_id IN (?2)", nativeQuery = true)
    void assignQuestionsToExam(Integer examId, List<Integer> questionIds);

    @Query("SELECT q FROM Question q WHERE q.subject.subjectId = :id")
    List<Question> findBySubjectId(@Param("id") Integer subjectId);

    List<Question> findByQuestionTextContainingIgnoreCase(String keyword);
    @Query("SELECT q.questionId FROM Question q WHERE q.exam.examId = ?1")
    List<Integer> findQuestionIdsByExamId(Integer examId);
    List<Question> findBySubject_SubjectId(Integer subjectId);
    List<Question> findBySubject_SubjectIdAndQuestionTextContainingIgnoreCase(Integer subjectId, String keyword);

    List<Question> findBySubject_SubjectIdAndExamIsNull(Integer subjectId);

    @Query("SELECT q FROM Question q WHERE q.subject.subjectId = :subjectId AND (q.exam IS NULL OR q.exam.examId = :currentExamId)")
    List<Question> findAvailableQuestions(@Param("subjectId") Integer subjectId, @Param("currentExamId") Integer currentExamId);

}
