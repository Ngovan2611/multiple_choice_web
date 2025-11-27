package haui.csn.thitot.service;


import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.repository.ExamRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamService {
    @Autowired
    private ExamRepository examRepository;
    public Exam getRandomExamBySubjectAndQuestions(Integer subjectId, int questionCount) {
        return examRepository.findRandomExamBySubjectAndQuestions(subjectId, questionCount);
    }

    public List<Exam> getExamByCreateBy() {
        return examRepository.findAllByTeacherRole();
    }
    public Exam getExamById(Integer id) {
        return examRepository.findById(id).get();
    }
    public Exam getExamByResultId(Integer resultId) {
        return examRepository.findExamByResultId(resultId);
    }
    public List<Exam> getAll() {
        return examRepository.findAll();
    }
    @Transactional
    public void addQuestionsToExam(Integer examId, List<Integer> questionIds) {
        for (Integer qId : questionIds) {
            examRepository.insert(examId, qId);
        }
    }
    public void save(Exam exam) {
        examRepository.save(exam);
    }

}
