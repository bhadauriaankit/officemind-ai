# OfficeMind AI

Private, self-hosted Enterprise AI Assistant. No confidential data ever leaves
company infrastructure — all LLM inference runs against Ollama/vLLM-hosted
models (Llama, Qwen, Mistral, DeepSeek).

> **Status:** Phase 1 / Module 1 — Project Initialization, Repository
> Structure, Docker Environment. Subsequent phases (Auth, Admin Portal,
> Knowledge Base, AI Engine, RAG, Agents, ...) are implemented incrementally,
> each behind its own review/approval gate.

## Repository layout

```
officemind-ai/
├── backend/                 Java 21 + Spring Boot 3, multi-module Maven build
│   ├── modules/
│   │   ├── common/           shared exceptions, value objects (no framework deps)
│   │   ├── domain/           DDD aggregates/entities (no framework deps)
│   │   ├── application/      use cases + outbound ports (interfaces only)
│   │   ├── infrastructure/    adapters: JPA, Redis, MinIO, Kafka, Qdrant clients
│   │   └── api/               REST controllers, security, OpenAPI
│   ├── app/                  runnable Spring Boot entrypoint, wiring + config
│   └── Dockerfile
├── frontend/                 React 18 + TypeScript + Tailwind + Vite
│   ├── src/app/               bootstrap (main.tsx, App.tsx, providers)
│   ├── src/shared/            api client, store, reusable UI
│   ├── src/features/          feature-sliced modules (system, auth, chat, ...)
│   └── Dockerfile
├── infra/
│   ├── docker-compose.yml     local dev stack: Postgres, Redis, MinIO, Kafka, Qdrant
│   ├── .env.example
│   ├── postgres/init/         bootstrap SQL (extensions)
│   └── k8s/base/              namespace, secrets template, StatefulSets/Deployments
└── .github/workflows/ci.yml   build + test + image build pipeline
```

## Architecture (Module 1 scope)

The backend follows **Clean Architecture / DDD** with strict dependency
direction: `api → application → domain`, with `infrastructure` implementing
ports defined in `application`. `domain` has zero framework dependencies so
business rules stay testable and portable.

As a first vertical slice proving the wiring end-to-end, Module 1 ships a
**platform health check**: `GET /api/v1/system/health` fans out through
`GetPlatformHealthUseCase` → `InfrastructureHealthPort` → real adapters for
Postgres, Redis, MinIO, Kafka, and Qdrant, and the React dashboard polls it
every 15s. This will back the Admin Portal's system dashboard (Phase 3) and
Kubernetes readiness probes (Phase 13).

## Running locally

```bash
cd infra
cp .env.example .env   # fill in real passwords
docker compose --env-file .env up -d --build
```

- Backend: http://localhost:8080/api/v1/system/health
- Frontend: http://localhost:3000
- MinIO console: http://localhost:9001
- Qdrant dashboard: http://localhost:6333/dashboard

## Testing

```bash
cd backend && mvn clean verify     # unit tests (application module) +
                                    # Testcontainers integration test (app module)
cd frontend && npm run test        # vitest
```

## Kubernetes (dev-grade manifests)

```bash
kubectl apply -f infra/k8s/base/
```

These manifests are intentionally dev-grade (StatefulSets with basic PVCs,
plain `Secret` objects). Phase 13 hardens this with Helm charts, HPAs,
network policies, and a real secrets manager (Vault / External Secrets).

## What's intentionally NOT in Module 1

Per the phased plan, the following are out of scope until their respective
modules are approved: Keycloak/JWT auth (Phase 2), Admin Portal UI (Phase 3),
document ingestion/RAG (Phase 4/6), Spring AI + Ollama integration (Phase 5),
agent framework (Phase 7), and production-hardened Helm/K8s (Phase 13).
