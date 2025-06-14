package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность {@code User}, представляющая пользователя в системе.
 *
 * <p>Класс аннотирован для работы с JPA и Hibernate, используется в качестве ORM-модели
 * и отражается в таблицу базы данных с именем {@code users}.</p>
 *
 * <p>Особенности:</p>
 * <ul>
 *   <li>{@link Entity} — класс управляется JPA и представляет таблицу в БД.</li>
 *   <li>{@link Table} — имя таблицы явно указано как {@code users}.</li>
 *   <li>{@code @Id} и {@code @GeneratedValue} — поле {@code id} является первичным ключом с автоинкрементом.</li>
 *   <li>{@code @Column(nullable = false)} — поля {@code name}, {@code email}, {@code createdAt} не могут быть {@code null}.</li>
 *   <li>{@code @Column(unique = true)} — значение {@code email} должно быть уникальным.</li>
 *   <li>{@code LocalDateTime createdAt} — дата и время создания пользователя.</li>
 * </ul>
 */
@Builder
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя (первичный ключ).
     * Генерируется автоматически при сохранении в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя. Обязательное поле.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Email пользователя. Обязательное и уникальное поле.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Возраст пользователя.
     */
    private int age;

    /**
     * Дата и время создания пользователя. Не может быть {@code null}.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
