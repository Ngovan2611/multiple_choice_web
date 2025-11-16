package haui.csn.thitot.service;


import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.repository.ExamRepository;
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
    public Exam getExamById(Integer examId) {
        return examRepository.findById(examId).get();
    }
    public Exam getExamByResultId(Integer resultId) {
        return examRepository.findExamByResultId(resultId);
    }
    public Exam getExamBySubject(Integer subjectId) {
        return examRepository.findFirstBySubject_SubjectId(subjectId);
    }

}
