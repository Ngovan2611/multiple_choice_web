package haui.csn.thitot.service;

import haui.csn.thitot.entity.User;
import haui.csn.thitot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
