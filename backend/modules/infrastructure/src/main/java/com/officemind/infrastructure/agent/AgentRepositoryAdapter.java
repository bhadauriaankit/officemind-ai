package com.officemind.infrastructure.agent;

import com.officemind.application.agent.AgentRepositoryPort;
import com.officemind.domain.agent.Agent;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AgentRepositoryAdapter implements AgentRepositoryPort {

    private final AgentJpaRepository jpaRepository;

    public AgentRepositoryAdapter(AgentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Agent save(Agent agent) {
        return AgentMapper.toDomain(jpaRepository.save(AgentMapper.toJpa(agent)));
    }

    @Override
    public Optional<Agent> findById(EntityId id) {
        return jpaRepository.findById(id.value()).map(AgentMapper::toDomain);
    }

    @Override
    public Optional<Agent> findByKey(String key) {
        return jpaRepository.findByKey(key).map(AgentMapper::toDomain);
    }

    @Override
    public List<Agent> findAll() {
        return jpaRepository.findAll().stream().map(AgentMapper::toDomain).toList();
    }

    @Override
    public void deleteById(EntityId id) {
        jpaRepository.deleteById(id.value());
    }
}