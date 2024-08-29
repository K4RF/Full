package solo.blog.entity.database.tx;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    private String loginId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String password;

    public Member() {

    }

    public Member(String loginId, String name, String password){
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }
}