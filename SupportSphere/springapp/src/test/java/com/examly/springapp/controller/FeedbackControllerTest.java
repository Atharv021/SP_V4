package com.examly.springapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.Ticket;
import com.examly.springapp.model.User;
import com.examly.springapp.model.enums.UserRole;
import com.examly.springapp.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.examly.springapp.repository.ErrorLogRepo;
import com.examly.springapp.config.JwtUtils;
import com.examly.springapp.config.MyUserDetailsService;

@WebMvcTest(controllers = FeedbackController.class)
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

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
    private User clientUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        clientUser = new User();
        clientUser.setUserId(1L);
        clientUser.setEmail("client@test.com");
        clientUser.setUserRole(UserRole.CLIENT);
        clientUserPrinciple = new UserPrinciple(clientUser);
    }
    
    private void authenticateAsClient() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(clientUserPrinciple, null, clientUserPrinciple.getAuthorities()));
    }

    @Test
    void testAddFeedback() throws Exception {
        authenticateAsClient();

        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);
        
        Feedback feedback = new Feedback();
        feedback.setFeedbackText("Good service");
        feedback.setRating(5);
        feedback.setCategory("General");
        feedback.setTicket(ticket);

        Feedback savedFeedback = new Feedback();
        savedFeedback.setFeedbackId(10L);
        savedFeedback.setFeedbackText("Good service");
        savedFeedback.setRating(5);
        savedFeedback.setUser(clientUser);
        savedFeedback.setTicket(ticket);

        when(feedbackService.createFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        mockMvc.perform(post("/api/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback))
                .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.feedbackId").value(10));
    }

    @Test
    void testGetFeedbackById() throws Exception {
        authenticateAsClient();

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(2L);
        feedback.setUser(clientUser);

        when(feedbackService.getFeedbackById(2L)).thenReturn(feedback);

        mockMvc.perform(get("/api/feedback/2")
                .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedbackId").value(2));
    }
}
