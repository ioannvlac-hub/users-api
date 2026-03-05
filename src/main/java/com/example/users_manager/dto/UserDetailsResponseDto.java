package com.example.users_manager.dto;

import com.example.users_manager.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponseDto {

    private Long id;
    private String name;
    private String surname;
    private Gender gender;
    private LocalDate birthdate;

    private String homeAddress;
    private String workAddress;
}