package com.jetrental.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // FIXED: Integer instead of int
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String pilotLicense;
    private String countryCode;
}