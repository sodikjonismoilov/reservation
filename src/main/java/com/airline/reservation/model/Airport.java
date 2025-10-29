package com.airline.reservation.model;


import jakarta.persistence.*;

@Entity
@Table(name = "airports")
public  class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 5)
    private String code;                  //  e.g. "JFK", "LAX"

    @Column(nullable = false)
    private String name;                 // John F. Kennedy International Airport

    @Column(nullable = false)
    private String city;                  // New York City

    @Column(nullable = false)
    private String country;                 // United States of America

    // Empty constructor is needed for Hibernate
    public Airport() {}


    public Airport(String code, String name, String city, String country) {
        this.code = code;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
}
