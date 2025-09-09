package org.example.databaseservice.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "login", nullable = false, length = Integer.MAX_VALUE)
    private String login;

    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @NotNull
    @Column(name = "nickname", nullable = false, length = Integer.MAX_VALUE)
    private String nickname;

    @NotNull
    @Column(name = "role", nullable = false, length = Integer.MAX_VALUE)
    private String role;

}