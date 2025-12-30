package com.org.bgv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "reference_sequence",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seq_type", "year"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferenceSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seq_type", nullable = false)
    private String seqType; // CANDIDATE, CASE, CHECK

    @Column(nullable = false)
    private int year;

    @Column(name = "current_value", nullable = false)
    private long currentValue;
}
