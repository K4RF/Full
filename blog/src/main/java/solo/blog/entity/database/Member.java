package solo.blog.entity.database;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Member {

    private String memberId;

    @NotBlank(message = "ID는 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,16}$", message = "ID는 영문과 숫자만 사용하며 1~16자 이내여야 합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8~16자 이내여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9 ]+$", message = "이름에는 특수 문자를 사용할 수 없습니다.")
    private String name;

    public Member() {

    }

    public Member(String memberId, String loginId, String name, String password){
        this.memberId = memberId;
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }
}