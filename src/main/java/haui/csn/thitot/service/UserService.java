package haui.csn.thitot.service;

import haui.csn.thitot.entity.User;
import haui.csn.thitot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;


    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
    public void save(User user) {
        userRepository.save(user);
    }
    public List<User> getUsersByRole(String role) {
        return userRepository.findUserByRole(role);
    }
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    public boolean existsUserByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(int id) {
        return userRepository.findUserById(id);
    }
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    public List<User> getStudentsByTeacherClass(String teacherClass, String keyword, String sortDirection) {
        // 1. Xử lý sắp xếp (Dùng JpaSort.unsafe để tránh lỗi dấu gạch dưới)
        Sort sort = JpaSort.unsafe(Sort.Direction.ASC, "full_name");
        if ("desc".equalsIgnoreCase(sortDirection)) {
            sort = JpaSort.unsafe(Sort.Direction.DESC, "full_name");
        }

        // 2. Xử lý từ khóa
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 3. Gọi Repository với lớp của giáo viên
        return userRepository.findByClassAndName(teacherClass, keyword, sort);
    }
}
