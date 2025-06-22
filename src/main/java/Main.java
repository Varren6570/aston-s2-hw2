import dao.UserDaoImpl;
import model.User;
import service.UserService;
import service.UserServiceImpl;
import util.HibernateUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserServiceImpl(new UserDaoImpl());

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/userdb");
        properties.setProperty("hibernate.connection.username", "postgres");
        properties.setProperty("hibernate.connection.password", "228359");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        HibernateUtil.init(properties);

        while (true) {
            System.out.println("\n--- Меню ---");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Получить пользователя по ID");
            System.out.println("3. Обновить пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("5. Показать всех пользователей");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            switch (scanner.nextLine()) {
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

    private static void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        try {
            userService.createUser(name, email, age);
            System.out.println("Пользователь создан.");
        } catch (Exception e) {
            System.out.println("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());

        try {
            User user = userService.getUserById(id);
            System.out.println("Найден: " + user);
        } catch (NoSuchElementException e) {
            System.out.println("Пользователь не найден.");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());

        System.out.print("Новое имя: ");
        String name = scanner.nextLine();

        System.out.print("Новый email: ");
        String email = scanner.nextLine();

        System.out.print("Новый возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        try {
            userService.updateUser(id, name, email, age);
            System.out.println("Пользователь обновлён.");
        } catch (NoSuchElementException e) {
            System.out.println("Пользователь не найден.");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());

        try {
            userService.deleteUserById(id);
            System.out.println("Пользователь удалён.");
        } catch (NoSuchElementException e) {
            System.out.println("Пользователь не найден.");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void listUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Пользователей нет.");
        } else {
            users.forEach(System.out::println);
        }
    }
}
