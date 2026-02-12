package com.jetrental.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Jet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // FIXED: Integer instead of int
    private String brand;
    private String model;
    private double pricePerDay;
    private int seats;
    @Column(unique = true)
    private String tailNumber;
    private boolean available;
}