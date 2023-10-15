package rocketseat.java.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

// @Getter ou @Setter para gerar apenas um getter ou setter, e pode ser usado em cima de um atributo ou classe.
@Data // @Data is a Lombok annotation to create all the getters, setters, equals, hash, and toString methods, based on the fields.
@Entity(name = "tb_users") // @Entity is a JPA annotation to declare the class as an entity.
public class UserModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String name;
    @Column(name = "usuario", unique = true) // @Column is a JPA annotation to customize the column name.
    private String username;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;     
}
