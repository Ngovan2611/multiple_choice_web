package haui.csn.thitot.service;


import haui.csn.thitot.entity.Question;
import haui.csn.thitot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getAllQuestionsByExam_Id(int exam_id) {
        return questionRepository.findByExam_ExamId(exam_id);
    }

    public List<Question> getAllBySubject_Id(int subject_id) {
        return questionRepository.findBySubjectId(subject_id);
    }
}

