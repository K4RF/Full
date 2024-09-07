package solo.blog.entity.v2;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
    private String redirectURL; // redirectURL 필드 추가
}