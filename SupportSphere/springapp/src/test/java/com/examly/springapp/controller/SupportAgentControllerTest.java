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

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.examly.springapp.model.SupportAgent;
import com.examly.springapp.service.SupportAgentService;
import com.examly.springapp.model.enums.SupportAgentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.examly.springapp.repository.ErrorLogRepo;
import com.examly.springapp.config.JwtUtils;
import com.examly.springapp.config.MyUserDetailsService;

@WebMvcTest(controllers = SupportAgentController.class)
public class SupportAgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupportAgentService supportAgentService;

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testAddSupportAgent() throws Exception {
        SupportAgent agent = new SupportAgent();
        agent.setEmail("agent@test.com");
        agent.setName("Agent One");
        agent.setPhone("1234567890");
        agent.setExpertise("Networking");
        agent.setExperience("2 years");
        agent.setStatus(SupportAgentStatus.AVAILABLE);
        agent.setShiftTiming("Morning");
        agent.setRemarks("Good");

        SupportAgent savedAgent = new SupportAgent();
        savedAgent.setAgentId(1L);
        savedAgent.setEmail("agent@test.com");

        when(supportAgentService.addSupportAgent(any(SupportAgent.class))).thenReturn(savedAgent);

        mockMvc.perform(post("/api/supportAgent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agentId").value(1));
    }

    @Test
    void testGetSupportAgentById() throws Exception {
        SupportAgent agent = new SupportAgent();
        agent.setAgentId(2L);
        agent.setEmail("agent2@test.com");

        when(supportAgentService.getSupportAgentById(2L)).thenReturn(Optional.of(agent));

        mockMvc.perform(get("/api/supportAgent/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentId").value(2));
    }

    @Test
    void testDeleteSupportAgent() throws Exception {
        SupportAgent agent = new SupportAgent();
        agent.setAgentId(3L);
        agent.setEmail("agent3@test.com");

        when(supportAgentService.deleteSupportAgent(3L)).thenReturn(agent);

        mockMvc.perform(delete("/api/supportAgent/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentId").value(3));
    }
}
