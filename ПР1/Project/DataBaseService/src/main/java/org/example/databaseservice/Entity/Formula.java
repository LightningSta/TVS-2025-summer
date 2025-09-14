package org.example.databaseservice.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.lang.annotation.Documented;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "formulas")
public class Formula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "formula", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> formula;

}