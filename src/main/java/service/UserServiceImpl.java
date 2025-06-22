package service;

import dao.UserDao;
import model.User;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void createUser(String name, String email, int age) {
        if (name == null || email == null) {
            throw new IllegalArgumentException("Имя и email обязательны");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);
        log.info("Пользователь создан: {}", user);
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }

        User user = userDao.findById(id);
        if (user == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        log.info("Найден пользователь: {}", user);
        return user;
    }

    @Override
    public void updateUser(Long id, String name, String email, int age) {
        User user = getUserById(id);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        userDao.update(user);
        log.info("Пользователь обновлён: {}", user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = getUserById(id);
        userDao.delete(user);
        log.info("Пользователь удалён: {}", user);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userDao.findAll();
        log.info("Получено пользователей: {}", users.size());
        return users;
    }
}
