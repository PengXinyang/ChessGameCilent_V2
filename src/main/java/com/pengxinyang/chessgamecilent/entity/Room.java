package com.pengxinyang.chessgamecilent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private Integer roomId;
    private String roomName;
    private Integer uidRed;//红方uid
    private Integer uidBlack;//黑方uid
}
