package solo.blog.entity.database.tx;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @SequenceGenerator(name = "member_seq_generator", sequenceName = "MEMBER_SEQ", allocationSize = 1)
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