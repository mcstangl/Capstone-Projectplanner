package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.enums.UserRole;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "pp_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "login_name", nullable = false, unique = true)
    private String loginName;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private UserRole role;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(loginName, that.loginName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName);
    }
}
