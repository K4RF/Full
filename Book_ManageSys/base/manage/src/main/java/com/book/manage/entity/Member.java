package com.book.manage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @NotBlank(message = "ID는 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]{1,16}$", message = "ID는 영문, 숫자, 밑줄(_)만 사용하며 1~16자 이내여야 합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9 ]+$", message = "닉네임에는 특수 문자를 사용할 수 없습니다.")
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 카카오 고유 ID 필드 추가 (nullable = true)
    @Column(nullable = true)
    private Long kakaoId;

    public Member() {
    }

    public Member(String loginId, String nickname, String password) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.password = password;
    }
}