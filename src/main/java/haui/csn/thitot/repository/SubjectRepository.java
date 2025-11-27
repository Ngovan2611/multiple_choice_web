package haui.csn.thitot.repository;

import haui.csn.thitot.entity.Subject;
import org.hibernate.sql.Update;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Optional<Subject> findById(Integer id);

}
