package dao;

import lombok.extern.slf4j.Slf4j;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

/**
 * Реализация интерфейса {@link UserDao} с использованием Hibernate.
 * Выполняет CRUD-операции над сущностью {@link User} через ORM.
 *
 * <p>Каждый метод автоматически логирует действия и ошибки с использованием SLF4J (Logback).</p>
 */
@Slf4j
public class UserDaoImpl implements UserDao {

    /**
     * Сохраняет нового пользователя в базу данных.
     *
     * @param user объект {@link User}, который нужно сохранить
     */
    @Override
    public void save(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            log.info("Пользователь сохранён: {}", user);
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            log.error("Ошибка при сохранении пользователя", e);
        }
    }

    /**
     * Ищет пользователя по его ID.
     *
     * @param id идентификатор пользователя
     * @return объект {@link User}, если найден; иначе {@code null}
     */
    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(User.class, id);
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя по ID: {}", id, e);
            return null;
        }
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список объектов {@link User}, или пустой список в случае ошибки
     */
    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            return List.of();
        }
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param user объект {@link User} с новыми данными
     */
    @Override
    public void update(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
            log.info("Пользователь обновлён: {}", user);
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            log.error("Ошибка при обновлении пользователя", e);
        }
    }

    /**
     * Удаляет пользователя из базы данных.
     *
     * @param user объект {@link User}, которого нужно удалить
     */
    @Override
    public void delete(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(user);
            tx.commit();
            log.info("Пользователь удалён: {}", user);
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            log.error("Ошибка при удалении пользователя", e);
        }
    }
}
