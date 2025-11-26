package haui.csn.thitot.repository;

import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
    List<User> findUserByRole(String role);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    User findUserById(int id);
    void deleteUserById(int id);
    @Query("SELECT u FROM User u WHERE u.role = 'student' " +
            "AND u.className = :className " +
            "AND (:keyword IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findByClassAndName(@Param("className") String className,
                                  @Param("keyword") String keyword,
                                  Sort sort);
}
