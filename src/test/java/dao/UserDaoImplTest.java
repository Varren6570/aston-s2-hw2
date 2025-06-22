package dao;

import lombok.extern.slf4j.Slf4j;
import model.User;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Testcontainers
class UserDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("userdb-test")
            .withUsername("postgres-test")
            .withPassword("postgres-test");
    private UserDao userDao;

    @BeforeAll
    static void setUpALl() {

        Properties props = new Properties();
        props.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        props.setProperty("hibernate.connection.username", postgres.getUsername());
        props.setProperty("hibernate.connection.password", postgres.getPassword());
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        HibernateUtil.init(props);
    }

    @AfterAll
    static void tearDownAll() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка при очистке таблицы", e);
        }

        userDao = new UserDaoImpl();
    }

    @Test
    void save_shouldSaveSuccessfullyWhenInputIsValidIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        //When
        userDao.save(user);

        //Then
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertEquals(1, result.size());
            assertEquals("Name", result.getFirst().getName());
            assertEquals("Mail", result.getFirst().getEmail());
            assertEquals(10, result.getFirst().getAge());
        }
    }


    @Test
    void save_shouldThrowExceptionWhenUserIsNullIT() {
        //Given
        User user = null;

        //When / Then
        assertThrows(IllegalArgumentException.class, () -> userDao.save(user));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertEquals(0, result.size());
        }
    }

    @Test
    void save_shouldCorrectlyRollbackWhenFailedToWriteDuplicateIT() {
        // given

        String name1 = "FirstName";
        String email1 = "Mail";
        int age1 = 10;

        User user1 = User.builder()
                .name(name1)
                .email(email1)
                .age(age1)
                .createdAt(LocalDateTime.now())
                .build();

        String name2 = "SecondName";
        String email2 = "Mail";
        int age2 = 10;

        User user2 = User.builder()
                .name(name2)
                .email(email2)
                .age(age2)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        userDao.save(user1);
        assertThrows(Exception.class, () -> userDao.save(user2));

        // then
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertEquals(1, result.size());
            assertEquals("FirstName", result.getFirst().getName());
        }
    }

    @Test
    void findById_ShouldFindCorrectlyByIdIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);


        //When
        User found = userDao.findById(user.getId());

        //Then
        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals(user.getName(), found.getName());
    }

    @Test
    void findById_ShouldReturnNullWhenThereIsNoElementWithSuchIdIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);
        //When / Then
        assertNull(userDao.findById(999L));
    }

    @Test
    void findById_ShouldReturnNullWhenIdIsNullIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);
        //When / Then
        assertNull(userDao.findById(null));
    }

    @Test
    void findAll_ShouldReturnTheListCorrectlyIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);

        String name2 = "Name2";
        String email2 = "Mail2";
        int age2 = 12;

        User user2 = User.builder()
                .name(name2)
                .email(email2)
                .age(age2)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user2);
        //When
        userDao.findAll();
        // / Then
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(2, result.size());
            assertEquals(user.getId(), result.getFirst().getId());
            assertEquals(user.getName(), result.getFirst().getName());
            assertEquals(user.getEmail(), result.getFirst().getEmail());
            assertEquals(user2.getId(), result.get(1).getId());
            assertEquals(user2.getName(), result.get(1).getName());
            assertEquals(user2.getEmail(), result.get(1).getEmail());
        }
    }

    @Test
    void findAll_ShouldReturnTheEmptyListIT() {
        //Given nothing. Db is Empty

        //When
        userDao.findAll();
        // / Then
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void update_shouldUpdateSuccessfullyWhenInputIsValidIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);

        String newName = "NewName";
        String newEmail = "NewMail";
        int newAge = 11;

        User found = userDao.findById(user.getId());
        found.setName(newName);
        found.setEmail(newEmail);
        found.setAge(newAge);

        //When
        userDao.update(found);

        //Then
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertEquals(1, result.size());
            assertEquals("NewName", result.getFirst().getName());
            assertEquals("NewMail", result.getFirst().getEmail());
            assertEquals(11, result.getFirst().getAge());
            assertEquals(user.getId(), result.getFirst().getId());

        }
    }


    @Test
    void update_shouldThrowExceptionWhenProvidedUserIsNullIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);

        //When / Then

        assertThrows(IllegalArgumentException.class, () -> userDao.update(null));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertEquals(1, result.size());
            assertEquals("Name", result.getFirst().getName());
            assertEquals("Mail", result.getFirst().getEmail());
            assertEquals(10, result.getFirst().getAge());
        }
    }

    @Test
    void delete_ShouldCorrectlyDeleteUserIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);
        //When
        userDao.delete(user);

        //Then
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertEquals(0, result.size());
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void delete_ShouldThrowExceptionWhenProvidedUserIsNullIT() {
        //Given
        String name = "Name";
        String email = "Mail";
        int age = 10;

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);

        //When / Then

        assertThrows(IllegalArgumentException.class, () -> userDao.delete(null));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> result = session.createQuery("FROM User", User.class).list();
            assertEquals(1, result.size());
            assertEquals("Name", result.getFirst().getName());
            assertEquals("Mail", result.getFirst().getEmail());
            assertEquals(10, result.getFirst().getAge());
        }
    }
}