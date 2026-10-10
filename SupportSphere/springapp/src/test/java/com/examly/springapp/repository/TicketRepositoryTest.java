package com.examly.springapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.examly.springapp.model.SupportAgent;
import com.examly.springapp.model.Ticket;
import com.examly.springapp.model.User;
import com.examly.springapp.model.enums.TicketStatus;
import com.examly.springapp.model.enums.SupportAgentStatus;
import com.examly.springapp.model.enums.TicketPriority;
import com.examly.springapp.model.enums.UserRole;
import com.examly.springapp.repository.TicketRepo;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.repository.SupportAgentRepo;

@DataJpaTest
public class TicketRepositoryTest {

    @Autowired
    private TicketRepo ticketRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private SupportAgentRepo supportAgentRepository;

    @Test
    void testSaveAndRetrieveTicket() {
        User user = new User();
        user.setEmail("client@test.com");
        user.setUsername("Client");
        user.setPassword("password123");
        user.setUserRole(UserRole.CLIENT);
        user.setMobileNumber("1234567890");
        user = userRepository.save(user);

        Ticket ticket = new Ticket();
        ticket.setTitle("Network issue");
        ticket.setDescription("Cannot connect to VPN");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedDate(LocalDate.now());
        ticket.setIssueCategory("Network");
        ticket.setUser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        assertThat(savedTicket.getTicketId()).isNotNull();

        Ticket retrieved = ticketRepository.findById(savedTicket.getTicketId()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTitle()).isEqualTo("Network issue");
    }

    @Test
    void testFindByUserId() {
        User user = new User();
        user.setEmail("client2@test.com");
        user.setUsername("Client2");
        user.setPassword("password123");
        user.setUserRole(UserRole.CLIENT);
        user.setMobileNumber("1234567890");
        user = userRepository.save(user);

        Ticket ticket = new Ticket();
        ticket.setTitle("Issue 1");
        ticket.setDescription("Issue 1 Desc");
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setIssueCategory("Hardware");
        ticket.setCreatedDate(LocalDate.now());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setUser(user);
        ticketRepository.save(ticket);

        List<Ticket> tickets = ticketRepository.findByUserUserId(user.getUserId());
        assertThat(tickets).hasSize(1);
    }

    @Test
    void testFindBySupportAgentId() {
        User user = new User();
        user.setEmail("client3@test.com");
        user.setUsername("Client3");
        user.setPassword("password123");
        user.setUserRole(UserRole.CLIENT);
        user.setMobileNumber("1234567890");
        user = userRepository.save(user);

        SupportAgent agent = new SupportAgent();
        agent.setEmail("agent@test.com");
        agent.setName("Agent");
        agent.setPhone("1234567890");
        agent.setExpertise("Networking");
        agent.setExperience("5 years");
        agent.setStatus(SupportAgentStatus.AVAILABLE);
        agent.setAddedDate(LocalDate.now());
        agent.setShiftTiming("Night");
        agent.setRemarks("Great");
        agent = supportAgentRepository.save(agent);

        Ticket ticket = new Ticket();
        ticket.setTitle("Issue 2");
        ticket.setDescription("Issue 2 Desc");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setIssueCategory("Network");
        ticket.setCreatedDate(LocalDate.now());
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUser(user);
        ticket.setSupportAgent(agent);
        ticketRepository.save(ticket);

        List<Ticket> tickets = ticketRepository.findBySupportAgentAgentId(agent.getAgentId());
        assertThat(tickets).hasSize(1);
    }
}
