package com.examly.springapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

// Every handled error is saved in the separate "ErrorLogs" table (SRS requirement).
@Getter
@Setter
@Entity
@Table(name = "ErrorLogs")
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorId;

    private LocalDateTime timestamp;

    private Integer status;

    @Column(length = 1000)
    private String message;

    @Column(length = 500)
    private String path;

    private String exceptionType;

    public ErrorLog() {
    }

    public ErrorLog(Integer status, String message, String path, String exceptionType) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.path = path;
        this.exceptionType = exceptionType;
    }
}
