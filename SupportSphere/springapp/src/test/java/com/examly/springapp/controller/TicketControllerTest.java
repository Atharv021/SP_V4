package com.examly.springapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.examly.springapp.config.UserPrinciple;
import com.examly.springapp.model.Ticket;
import com.examly.springapp.model.User;
import com.examly.springapp.model.enums.TicketPriority;
import com.examly.springapp.model.enums.TicketStatus;
import com.examly.springapp.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.examly.springapp.model.enums.UserRole;
import com.examly.springapp.repository.ErrorLogRepo;
import com.examly.springapp.config.JwtUtils;
import com.examly.springapp.config.MyUserDetailsService;

@WebMvcTest(controllers = TicketController.class)
public class TicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private ErrorLogRepo errorLogRepo;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private UserPrinciple clientUserPrinciple;
    private UserPrinciple managerUserPrinciple;
    private User clientUser;

    @BeforeEach
    void setUp() {
        // Setup mock security context without the full filter chain
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        clientUser = new User();
        clientUser.setUserId(1L);
        clientUser.setEmail("client@test.com");
        clientUser.setUserRole(UserRole.CLIENT);

        clientUserPrinciple = new UserPrinciple(clientUser);

        User managerUser = new User();
        managerUser.setUserId(2L);
        managerUser.setEmail("manager@test.com");
        managerUser.setUserRole(UserRole.MANAGER);
        managerUserPrinciple = new UserPrinciple(managerUser);
    }

    private void authenticateAsClient() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(clientUserPrinciple, null, clientUserPrinciple.getAuthorities()));
    }

    private void authenticateAsManager() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(managerUserPrinciple, null, managerUserPrinciple.getAuthorities()));
    }

    @Test
    void testAddTicket() throws Exception {
        authenticateAsClient();

        Ticket ticket = new Ticket();
        ticket.setTitle("App crashing");
        ticket.setDescription("App crashes on launch");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setIssueCategory("Software");

        Ticket savedTicket = new Ticket();
        savedTicket.setTicketId(10L);
        savedTicket.setTitle(ticket.getTitle());
        savedTicket.setUser(clientUser);
        
        when(ticketService.addTicket(any(Ticket.class))).thenReturn(savedTicket);

        mockMvc.perform(post("/api/ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket))
                .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(10));
    }

    @Test
    void testGetTicketById_Success() throws Exception {
        authenticateAsClient();

        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);
        ticket.setUser(clientUser);

        when(ticketService.getTicketById(1L)).thenReturn(Optional.of(ticket));

        mockMvc.perform(get("/api/ticket/1")
                .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(1));
    }

    @Test
    void testGetTicketById_AccessDeniedForOtherClient() throws Exception {
        authenticateAsClient();

        User otherClient = new User();
        otherClient.setUserId(99L);
        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);
        ticket.setUser(otherClient);

        when(ticketService.getTicketById(1L)).thenReturn(Optional.of(ticket));

        mockMvc.perform(get("/api/ticket/1")
                .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isForbidden()); // handled by global exception handler? 
                // wait, GlobalExceptionHandler handles AccessDeniedException and returns 403.
    }
}
