package com.officemind.api.agent;

import com.officemind.application.agent.CreateAgentUseCase;
import com.officemind.application.agent.DeleteAgentUseCase;
import com.officemind.application.agent.ListAgentsUseCase;
import com.officemind.application.agent.UpdateAgentUseCase;
import com.officemind.domain.agent.Agent;
import com.officemind.domain.shared.EntityId;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {

    private final ListAgentsUseCase listAgentsUseCase;
    private final CreateAgentUseCase createAgentUseCase;
    private final UpdateAgentUseCase updateAgentUseCase;
    private final DeleteAgentUseCase deleteAgentUseCase;

    public AgentController(ListAgentsUseCase listAgentsUseCase,
                            CreateAgentUseCase createAgentUseCase,
                            UpdateAgentUseCase updateAgentUseCase,
                            DeleteAgentUseCase deleteAgentUseCase) {
        this.listAgentsUseCase = listAgentsUseCase;
        this.createAgentUseCase = createAgentUseCase;
        this.updateAgentUseCase = updateAgentUseCase;
        this.deleteAgentUseCase = deleteAgentUseCase;
    }

    // Open to any authenticated user (not admin-gated): the chat UI needs
    // this to populate the agent picker for every employee, not just admins.
    @GetMapping
    public List<AgentResponse> list() {
        return listAgentsUseCase.execute().stream().map(AgentResponse::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AgentResponse create(@Valid @RequestBody AgentRequest request) {
        Agent agent = createAgentUseCase.execute(request.key(), request.name(), request.description(), request.systemPrompt());
        return AgentResponse.from(agent);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public AgentResponse update(@PathVariable UUID id, @Valid @RequestBody AgentRequest request) {
        Agent agent = updateAgentUseCase.execute(EntityId.of(id), request.name(), request.description(), request.systemPrompt());
        return AgentResponse.from(agent);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        deleteAgentUseCase.execute(EntityId.of(id));
    }
}
