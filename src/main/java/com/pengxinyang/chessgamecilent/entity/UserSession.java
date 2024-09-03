package com.pengxinyang.chessgamecilent.entity;

import lombok.Data;
import lombok.Getter;

@Data
public class UserSession {
    @Getter
    private static UserSession instance = new UserSession();
    private String token;
    private User user;

    private UserSession() {}

}

