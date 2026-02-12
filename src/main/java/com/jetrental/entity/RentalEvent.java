package com.jetrental.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Date;

@Entity
@Table(name = "RentalEvent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // FIXED: Integer instead of int

    @ManyToOne
    @JoinColumn(name = "jetId", nullable = false)
    private Jet jetRented;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customerRenting;

    private Date rentalDate;
    private Date returnDate;
    private double totalCost;
    private boolean isClosed;
}