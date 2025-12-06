package haui.csn.thitot.service;

import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Subject getById(int id) {
        return subjectRepository.findById(id)
                .orElse(null); // Trả về null nếu không tìm thấy
    }

    public void deleteById(int id) {
        subjectRepository.deleteById(id);
    }
    public Subject findById(Integer id) {
        return subjectRepository.findById(id).orElse(null);
    }
}
