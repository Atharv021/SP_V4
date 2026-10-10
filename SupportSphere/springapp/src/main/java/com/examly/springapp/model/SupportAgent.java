package com.examly.springapp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.examly.springapp.model.enums.SupportAgentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "support_agent")
public class SupportAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentId;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Expertise is required")
    private String expertise;

    @NotBlank(message = "Experience is required")
    private String experience;

    // Allowed values: Available, Unavailable.
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private SupportAgentStatus status;

    private LocalDate addedDate;

    // Profile / resume stored as a base64-encoded string in a large-object column. Optional.
    @Lob
    @Column(nullable = true)
    private String profile;

    @NotBlank(message = "Shift timing is required")
    private String shiftTiming;

    @NotBlank(message = "Remarks are required")
    @Column(length = 1000)
    private String remarks;

    public SupportAgent() {
    }
}
