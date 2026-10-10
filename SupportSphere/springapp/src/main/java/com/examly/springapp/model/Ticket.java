package com.examly.springapp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import com.examly.springapp.model.enums.TicketPriority;
import com.examly.springapp.model.enums.TicketStatus;
import com.examly.springapp.model.enums.TicketStatusConverter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @NotBlank(message = "Title is required")
    @Size(max = 120, message = "Title must not exceed 120 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(nullable = false, length = 2000)
    private String description;

    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    // Allowed values: Open, In Progress, Resolved, Closed. Every new ticket starts as Open.
    @NotNull(message = "Status is required")
    @Convert(converter = TicketStatusConverter.class)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = true)
    private LocalDate resolutionDate;

    @NotBlank(message = "Issue category is required")
    private String issueCategory;

    // Optional at first, but required before the ticket can be marked Resolved.
    @Column(nullable = true, length = 2000)
    private String resolutionSummary;

    @Column(nullable = true)
    private Boolean satisfied;

    // The client who raised the ticket.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The support agent assigned by the manager (empty until assigned).
    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = true)
    private SupportAgent supportAgent;

    public Ticket() {
    }
}
