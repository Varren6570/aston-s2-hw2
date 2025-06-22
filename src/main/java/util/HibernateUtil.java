package util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

/**
 * Класс для настройки и управления {@link SessionFactory} — основным компонентом Hibernate,
 * отвечающим за создание сессий (соединений с базой данных).
 *
 * <p>Предназначен для централизованного создания и управления {@code SessionFactory} в приложении.</p>
 *
 * <p>Особенности реализации:</p>
 * <ul>
 *   <li>Использует статический инициализатор для настройки Hibernate при загрузке класса.</li>
 *   <li>Регистрирует аннотированный класс {@code model.User} вручную через {@code configuration.addAnnotatedClass()}.</li>
 *   <li>Создаёт {@link ServiceRegistry} на основе настроек Hibernate, указанных в {@code hibernate.properties} .</li>
 * </ul>
 *
 * <p>Логирование прозводится через SLF4J.</p>
 */
@Slf4j
public class HibernateUtil {

    @Getter
    private static SessionFactory sessionFactory;

    /**
     * Явная инициализация Hibernate с заданными настройками.
     * Может быть вызвана из main-класса или из теста.
     *
     * @param settings Hibernate-настройки (URL, логин, пароль, диалект и т.д.)
     */
    public static void init(Properties settings) {
        if (sessionFactory != null) {
            log.warn("SessionFactory уже инициализирована. Повторная инициализация невозможна.");
            return;
        }

        try {
            Configuration configuration = new Configuration();
            configuration.setProperties(settings);

            // Регистрируем entity-классы
            configuration.addAnnotatedClass(model.User.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            log.info("Hibernate SessionFactory успешно инициализирована вручную.");

        } catch (Throwable ex) {
            log.error("Инициализация SessionFactory провалена.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    /**
     * Закрывает SessionFactory и освобождает ресурсы.
     */
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
            log.info("SessionFactory закрыта.");
        }
    }
}
