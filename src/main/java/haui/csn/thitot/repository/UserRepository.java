package haui.csn.thitot.repository;

import haui.csn.thitot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
    List<User> findUserByRole(String role);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    User findUserById(int id);
    void deleteUserById(int id);
}
