package rest.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA best practice
@Builder(toBuilder = true)
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
    }
)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 15)
    private String mobile;

    @Column(nullable = false)
    private Integer age;
}
