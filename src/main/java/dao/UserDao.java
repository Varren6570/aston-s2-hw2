package dao;

import model.User;
import java.util.List;

/**
 * Интерфейс доступа к данным пользователей (DAO - Data Access Object).
 * Определяет базовые CRUD-операции над сущностью {@link User}.
 */
public interface UserDao {

    /**
     * Сохраняет нового пользователя в базу данных.
     *
     * @param user объект {@link User}, который нужно сохранить
     */
    void save(User user);

    /**
     * Ищет пользователя по его уникальному идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект {@link User}, если найден; иначе {@code null}
     */
    User findById(Long id);

    /**
     * Возвращает список всех пользователей из базы данных.
     *
     * @return список объектов {@link User}
     */
    List<User> findAll();

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param user объект {@link User} с обновлёнными данными
     */
    void update(User user);

    /**
     * Удаляет пользователя из базы данных.
     *
     * @param user объект {@link User}, которого нужно удалить
     */
    void delete(User user);
}
