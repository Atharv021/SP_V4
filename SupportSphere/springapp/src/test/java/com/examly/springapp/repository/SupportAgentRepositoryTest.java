package com.examly.springapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.examly.springapp.model.SupportAgent;
import com.examly.springapp.model.enums.SupportAgentStatus;
import com.examly.springapp.repository.SupportAgentRepo;

@DataJpaTest
public class SupportAgentRepositoryTest {

    @Autowired
    private SupportAgentRepo supportAgentRepository;

    @Test
    void testSaveAndFindSupportAgent() {
        SupportAgent agent = new SupportAgent();
        agent.setEmail("newagent@test.com");
        agent.setName("New Agent");
        agent.setPhone("1234567890");
        agent.setExpertise("Networking");
        agent.setExperience("2 years");
        agent.setStatus(SupportAgentStatus.AVAILABLE);
        agent.setAddedDate(LocalDate.now());
        agent.setShiftTiming("Morning");
        agent.setRemarks("Good");

        SupportAgent saved = supportAgentRepository.save(agent);
        assertThat(saved.getAgentId()).isNotNull();

        Optional<SupportAgent> retrieved = supportAgentRepository.findById(saved.getAgentId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("newagent@test.com");
    }

    @Test
    void testFindByEmail() {
        SupportAgent agent = new SupportAgent();
        agent.setEmail("agent.email@test.com");
        agent.setName("Email Agent");
        agent.setPhone("0987654321");
        agent.setExpertise("Hardware");
        agent.setExperience("3 years");
        agent.setStatus(SupportAgentStatus.AVAILABLE);
        agent.setAddedDate(LocalDate.now());
        agent.setShiftTiming("Evening");
        agent.setRemarks("Excellent");
        supportAgentRepository.save(agent);

        SupportAgent retrieved = supportAgentRepository.findByEmail("agent.email@test.com");
        assertThat(retrieved).isNotNull();
    }
}
