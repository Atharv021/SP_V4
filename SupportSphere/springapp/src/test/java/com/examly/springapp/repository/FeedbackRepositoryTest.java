package com.examly.springapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.Ticket;
import com.examly.springapp.model.User;
import java.time.LocalDate;
import com.examly.springapp.model.enums.TicketStatus;
import com.examly.springapp.model.enums.TicketPriority;
import com.examly.springapp.model.enums.UserRole;
import com.examly.springapp.repository.FeedbackRepo;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.repository.TicketRepo;

@DataJpaTest
public class FeedbackRepositoryTest {

    @Autowired
    private FeedbackRepo feedbackRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private TicketRepo ticketRepository;

    @Test
    void testSaveAndFindFeedback() {
        User user = new User();
        user.setEmail("fbclient@test.com");
        user.setUsername("FBClient");
        user.setPassword("password123");
        user.setUserRole(UserRole.CLIENT);
        user.setMobileNumber("1234567890");
        user = userRepository.save(user);

        Ticket ticket = new Ticket();
        ticket.setTitle("Issue for FB");
        ticket.setDescription("Test Description");
        ticket.setPriority(TicketPriority.LOW);
        ticket.setIssueCategory("Other");
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setCreatedDate(LocalDate.now());
        ticket.setUser(user);
        ticket = ticketRepository.save(ticket);

        Feedback feedback = new Feedback();
        feedback.setFeedbackText("Excellent");
        feedback.setRating(5);
        feedback.setCategory("General");
        feedback.setDate(LocalDate.now());
        feedback.setTicket(ticket);
        feedback.setUser(user);

        Feedback saved = feedbackRepository.save(feedback);
        assertThat(saved.getFeedbackId()).isNotNull();

        Optional<Feedback> retrieved = feedbackRepository.findById(saved.getFeedbackId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getFeedbackText()).isEqualTo("Excellent");
    }

    @Test
    void testFindByUserId() {
        User user = new User();
        user.setEmail("fbclient2@test.com");
        user.setUsername("FBClient2");
        user.setPassword("password123");
        user.setUserRole(UserRole.CLIENT);
        user.setMobileNumber("1234567890");
        user = userRepository.save(user);

        Ticket ticket = new Ticket();
        ticket.setTitle("Issue");
        ticket.setDescription("Desc");
        ticket.setPriority(TicketPriority.LOW);
        ticket.setIssueCategory("Other");
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setCreatedDate(LocalDate.now());
        ticket.setUser(user);
        ticket = ticketRepository.save(ticket);

        Feedback feedback = new Feedback();
        feedback.setFeedbackText("Good");
        feedback.setRating(4);
        feedback.setCategory("General");
        feedback.setDate(LocalDate.now());
        feedback.setTicket(ticket);
        feedback.setUser(user);
        feedbackRepository.save(feedback);

        List<Feedback> result = feedbackRepository.findByUserUserId(user.getUserId());
        assertThat(result).hasSize(1);
    }
}
