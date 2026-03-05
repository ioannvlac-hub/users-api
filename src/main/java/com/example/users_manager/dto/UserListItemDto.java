package com.example.users_manager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListItemDto {
    private Long id;
    private String name;
    private String surname;
}