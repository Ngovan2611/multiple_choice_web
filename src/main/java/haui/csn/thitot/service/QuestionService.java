package haui.csn.thitot.service;


import haui.csn.thitot.entity.Question;
import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.repository.ExamRepository;
import haui.csn.thitot.repository.QuestionRepository;
import haui.csn.thitot.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ExamRepository examRepository;

    public List<Question> getAllQuestionsByExam_Id(int exam_id) {
        return questionRepository.findByExam_ExamId(exam_id);
    }

    // Lấy tất cả câu hỏi
    public List<Question> getAllQuestions() {

        return questionRepository.findAll();
    }
    // Lấy tất cả câu hỏi theo môn học
    public List<Question> getQuestionsBySubject(Integer subjectId) {
        return questionRepository.findBySubject_SubjectId (subjectId);
    }
    // Tìm theo keyword (nếu bạn dùng)
    public List<Question> search(String keyword) {
        return questionRepository.findByQuestionTextContainingIgnoreCase(keyword);
    }

    // Lấy 1 câu hỏi theo id
    public Question getQuestionsById(Integer id) {
        return questionRepository.findById(id)
                .orElse(null);
    }
    public Question getQuestionById(Integer id) {
        return questionRepository.findById(id).orElse(null);
    }

    // Tạo câu hỏi mới
    public Question createQuestion(Integer subjectId, String text, String imageUrl) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Question q = new Question();
        q.setSubject(subject);
        q.setQuestionText(text);
        q.setImageUrl(imageUrl);

        return questionRepository.save(q);
    }

    // Lưu (cho update)
    public void save(Question q) {
        questionRepository.save(q);
    }

    // Xóa câu hỏi
    public void delete(Integer id) {
        questionRepository.deleteById(id);
    }

    public Question findById(Integer id) {
        return questionRepository.findById(id).orElse(null);
    }

    // Phương thức mới: Tìm kiếm theo Môn học VÀ Từ khóa
    public List<Question> searchQuestionsBySubjectAndKeyword(Integer subjectId, String keyword) {
        // Gọi phương thức Repository mới
        return questionRepository.findBySubject_SubjectIdAndQuestionTextContainingIgnoreCase(subjectId, keyword);
    }

}
