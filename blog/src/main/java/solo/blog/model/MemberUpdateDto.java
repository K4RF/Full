package solo.blog.model;

import lombok.Data;

@Data
public class MemberUpdateDto {
    private Long memberId;  // memberId는 Long 타입이어야 합니다.
    private String name;
    private String password;

    // 생성자 정의
    public MemberUpdateDto(Long memberId, String name, String password) {
        this.memberId = memberId;
        this.name = name;
        this.password = password;
    }
}

