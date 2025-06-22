package service;

import dao.UserDao;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserServiceImpl(userDao);
    }

    @Test
    void createUser_shouldSaveUserCorrectly() {
        userService.createUser("Name", "Mail", 10);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("Name", saved.getName());
        assertEquals("Mail", saved.getEmail());
        assertEquals(10, saved.getAge());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void createUser_shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(null, "Mail", 10));
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        when(userDao.findById(1L)).thenReturn(user);

        User result = userService.getUserById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(null));
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {
        when(userDao.findById(1L)).thenReturn(null);
        assertThrows(NoSuchElementException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_shouldUpdateUserCorrectly() {
        User existingUser = new User();
        existingUser.setId(1L);
        when(userDao.findById(1L)).thenReturn(existingUser);

        userService.updateUser(1L, "Name", "Mail", 10);

        verify(userDao).update(existingUser);
        assertEquals("Name", existingUser.getName());
        assertEquals("Mail", existingUser.getEmail());
        assertEquals(10, existingUser.getAge());
    }

    @Test
    void deleteUserById_shouldDeleteUser() {
        User user = new User();
        user.setId(2L);
        when(userDao.findById(2L)).thenReturn(user);

        userService.deleteUserById(2L);

        verify(userDao).delete(user);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        List<User> users = List.of(new User(), new User());
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
    }
}
