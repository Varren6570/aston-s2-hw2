package service;

import model.User;

import java.util.List;

public interface UserService {
    void createUser(String name, String email, int age);
    User getUserById(Long id);
    List<User> getAllUsers();
    void updateUser(Long id, String name, String email, int age);
    void deleteUserById(Long id);
}
