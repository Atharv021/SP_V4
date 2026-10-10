package com.examly.springapp.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examly.springapp.exceptions.AgentDeletionException;
import com.examly.springapp.exceptions.DuplicateAgentException;
import com.examly.springapp.model.SupportAgent;
import com.examly.springapp.repository.FeedbackRepo;
import com.examly.springapp.repository.SupportAgentRepo;
import com.examly.springapp.repository.TicketRepo;
import com.examly.springapp.service.SupportAgentService;

@Service
public class SupportAgentServiceImpl implements SupportAgentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportAgentServiceImpl.class);

    private final SupportAgentRepo supportAgentRepo;
    private final TicketRepo ticketRepo;
    private final FeedbackRepo feedbackRepo;

    public SupportAgentServiceImpl(SupportAgentRepo supportAgentRepo, TicketRepo ticketRepo,
            FeedbackRepo feedbackRepo) {
        this.supportAgentRepo = supportAgentRepo;
        this.ticketRepo = ticketRepo;
        this.feedbackRepo = feedbackRepo;
    }

    @Override
    @Transactional
    public SupportAgent addSupportAgent(SupportAgent supportAgent) {
        String email = supportAgent.getEmail().trim().toLowerCase();

        // Two agents cannot share the same email
        if (supportAgentRepo.findByEmail(email) != null) {
            throw new DuplicateAgentException("A support agent with this email already exists");
        }

        supportAgent.setAgentId(null);
        supportAgent.setEmail(email);
        supportAgent.setAddedDate(LocalDate.now());
        supportAgent.setStatus(com.examly.springapp.model.enums.SupportAgentStatus.AVAILABLE);
        SupportAgent savedAgent = supportAgentRepo.save(supportAgent);
        LOGGER.info("Support agent created with id={}", savedAgent.getAgentId());
        return savedAgent;
    }

    @Override
    public Optional<SupportAgent> getSupportAgentById(Long agentId) {
        return supportAgentRepo.findById(agentId);
    }

    @Override
    public List<SupportAgent> getAllSupportAgents() {
        return supportAgentRepo.findAll().stream()
                .filter(a -> !com.examly.springapp.model.enums.SupportAgentStatus.DELETED.equals(a.getStatus()))
                .toList();
    }

    @Override
    @Transactional
    public SupportAgent updateSupportAgent(Long agentId, SupportAgent supportAgent) {
        SupportAgent existing = supportAgentRepo.findById(agentId)
                .orElseThrow(() -> new NoSuchElementException("Support agent not found with id " + agentId));

        String email = supportAgent.getEmail().trim().toLowerCase();
        SupportAgent sameEmail = supportAgentRepo.findByEmail(email);
        if (sameEmail != null && !sameEmail.getAgentId().equals(agentId)) {
            throw new DuplicateAgentException("A support agent with this email already exists");
        }

        existing.setName(supportAgent.getName());
        existing.setEmail(email);
        existing.setPhone(supportAgent.getPhone());
        existing.setExpertise(supportAgent.getExpertise());
        existing.setExperience(supportAgent.getExperience());
        existing.setStatus(supportAgent.getStatus());
        existing.setProfile(supportAgent.getProfile());
        existing.setShiftTiming(supportAgent.getShiftTiming());
        existing.setRemarks(supportAgent.getRemarks());
        // addedDate never changes after the agent is created
        SupportAgent savedAgent = supportAgentRepo.save(existing);
        LOGGER.info("Support agent updated with id={}", savedAgent.getAgentId());
        return savedAgent;
    }

    @Override
    @Transactional
    public SupportAgent deleteSupportAgent(Long agentId) {
        SupportAgent agent = supportAgentRepo.findById(agentId)
                .orElseThrow(() -> new NoSuchElementException("Support agent not found with id " + agentId));

        if (com.examly.springapp.model.enums.SupportAgentStatus.UNAVAILABLE.equals(agent.getStatus())) {
            throw new AgentDeletionException("Agents with active assignments cannot be deleted.");
        }

        // An agent that worked on tickets (or received feedback) is kept for the ticket history
        if (!ticketRepo.findBySupportAgentAgentId(agentId).isEmpty()
                || !feedbackRepo.findBySupportAgentAgentId(agentId).isEmpty()) {
            
            // Soft delete
            agent.setStatus(com.examly.springapp.model.enums.SupportAgentStatus.DELETED);
            supportAgentRepo.save(agent);
            LOGGER.info("Support agent soft-deleted with id={}", agentId);
            return agent;
        }

        supportAgentRepo.delete(agent);
        LOGGER.info("Support agent deleted with id={}", agentId);
        return agent;
    }
}
