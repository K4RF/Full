package com.book.manage.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Login {
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
}
