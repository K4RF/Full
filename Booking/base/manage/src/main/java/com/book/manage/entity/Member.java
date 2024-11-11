package com.book.manage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String memberId;

    private String loginId;

    private String password;

    private String nickname;

    public Member(){

    }

    public Member(String memberId, String loginId, String nickname, String password){
        this.memberId = memberId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.password = password;
    }
}
