package haui.csn.thitot.repository;


import haui.csn.thitot.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByExam_ExamId(Integer examId);

    // Lấy tất cả câu hỏi theo môn học
    List<Question> findByExam_Subject_SubjectId(Integer subjectId);


    // Tìm kiếm theo tên câu hỏi (option)
    List<Question> findByQuestionTextContainingIgnoreCase(String keyword);

    List<Question> findBySubject_SubjectId(Integer subjectId);

    // QuestionRepository.java (Giao diện)
    List<Question> findBySubject_SubjectIdAndQuestionTextContainingIgnoreCase(Integer subjectId, String keyword);
}
