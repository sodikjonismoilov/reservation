package com.airline.reservation.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "passengers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_passenger_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_passenger_passport", columnNames = "passport_number")
        },
        indexes = {
                @Index(name = "idx_passenger_lastname", columnList = "last_name"),
                @Index(name = "idx_passenger_email", columnList = "email")
        }
        )
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotBlank @Email @Size(max = 255)
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank @Size(max = 30)
    @Column(name = "passport_number", nullable = false, length = 30)
    private String passportNumber;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Passenger() {}

    public Passenger(String firstName, String lastName, String email, String passportNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passportNumber = passportNumber;
    }

    //getter and setters
    public Long getId() { return  id; }
    public void setId(Long id) {this.id = id;}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    }
