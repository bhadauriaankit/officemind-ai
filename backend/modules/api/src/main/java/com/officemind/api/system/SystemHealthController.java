package com.officemind.api.system;

import com.officemind.application.system.GetPlatformHealthUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes platform readiness for the Admin Portal dashboard (Phase 3) and
 * for Kubernetes readiness/liveness probes (Phase 13). Intentionally
 * unauthenticated at the network level only inside the cluster; exposed
 * externally it must sit behind the gateway's auth filter (Phase 2).
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemHealthController {

    private final GetPlatformHealthUseCase getPlatformHealthUseCase;

    public SystemHealthController(GetPlatformHealthUseCase getPlatformHealthUseCase) {
        this.getPlatformHealthUseCase = getPlatformHealthUseCase;
    }

    @GetMapping("/health")
    public ResponseEntity<GetPlatformHealthUseCase.PlatformHealthReport> health() {
        GetPlatformHealthUseCase.PlatformHealthReport report = getPlatformHealthUseCase.execute();
        return report.healthy() ? ResponseEntity.ok(report) : ResponseEntity.status(503).body(report);
    }
}
