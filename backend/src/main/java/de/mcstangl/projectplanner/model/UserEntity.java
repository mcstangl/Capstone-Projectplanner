package de.mcstangl.projectplanner.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@EntityScan
@Table(name = "user")
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false, unique = true)
    private long id;

    @Column(name = "login_name", nullable = false, unique = true)
    private String loginName;

    @Column(name = "password", nullable = false)
    private String password;


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
