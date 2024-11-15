package com.book.manage.entity.dto;

import lombok.Data;

@Data
public class MemberEditDto {
    private Long memberId;  // memberId는 Long 타입이어야 합니다.
    private String nickname;
    private String password;

    // 생성자 정의
    public MemberEditDto(Long memberId, String nickname, String password) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.password = password;
    }
}
