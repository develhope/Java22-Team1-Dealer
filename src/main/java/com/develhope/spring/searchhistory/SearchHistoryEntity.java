package com.develhope.spring.searchhistory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchId;

    @Column(nullable = false)
    private LocalDateTime searchDate;

    @Column(nullable = false)
    private String searchType;


    public SearchHistoryEntity(LocalDateTime searchDate, String searchType) {
        this.searchDate = searchDate;
        this.searchType = searchType;
    }
}