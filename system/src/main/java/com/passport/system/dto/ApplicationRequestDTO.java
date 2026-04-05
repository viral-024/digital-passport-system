package com.passport.system.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ApplicationRequestDTO {

    private Long userId;
    private String fullName;
    private LocalDate dob;
    private String address;
    
}