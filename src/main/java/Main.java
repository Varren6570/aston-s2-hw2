import dao.UserDao;
import dao.UserDaoImpl;
import lombok.extern.slf4j.Slf4j;
import model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


/**
 * Простое консольное приложение для CRUD-операций над сущностью User.
 * Операции производятся с помощью выбора номера желаемой опции в консоли.
 * Настройка Hibernate, произ ведена через hibernate.properties.
 * Логирование - Slf4j + Logback
 */
@Slf4j
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDaoImpl();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Меню ---");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Получить пользователя по ID");
            System.out.println("3. Обновить пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("5. Показать всех пользователей");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> createUser();
                case "2" -> findUserById();
                case "3" -> updateUser();
                case "4" -> deleteUser();
                case "5" -> listUsers();
                case "0" -> {
                    System.out.println("Выход.");
                    return;
                }
                default -> System.out.println("Неверный выбор. Повторите.");
            }
        }
    }

    /**
     * Создаёт нового пользователя на основе введённых данных.
     */
    private static void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);
        System.out.println("Пользователь создан: " + user);
    }

    /**
     * Ищет пользователя по ID и выводит его в консоль.
     */
    private static void findUserById() {
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());


        Optional<User> user = Optional.ofNullable(userDao.findById(id));
        user.ifPresentOrElse(
                u -> System.out.println("Найден: " + u),
                () -> System.out.println("Пользователь не найден.")
        );


    }

    /**
     * Обновляет пользователя по ID.
     */
    private static void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> optionalUser = Optional.ofNullable(userDao.findById(id));
        if (optionalUser.isEmpty()) {
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.print("Новое имя: ");
        String name = scanner.nextLine();

        System.out.print("Новый email: ");
        String email = scanner.nextLine();

        System.out.print("Новый возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = optionalUser.get();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        userDao.update(user);
        System.out.println("Пользователь обновлён: " + user);
    }

    /**
     * Удаляет пользователя по ID.
     */
    private static void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());

        try {
            userDao.delete(userDao.findById(id));
            System.out.println("Пользователь удалён (если существовал).");
        } catch (Exception e) {
            System.out.println("Неверный ID");
        }

    }

    /**
     * Выводит всех пользователей в консоль.
     */
    private static void listUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователей нет.");
        } else {
            users.forEach(System.out::println);
        }
    }
}
