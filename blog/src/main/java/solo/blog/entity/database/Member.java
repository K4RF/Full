package solo.blog.entity.database;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Member {

    private String memberId;

    @NotEmpty
    private String loginId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String password;

    public Member() {

    }

    public Member(String memberId, String loginId, String name, String password){
        this.memberId = memberId;
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }
}