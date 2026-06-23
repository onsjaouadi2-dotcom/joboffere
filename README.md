# JobBoard — Architecture Microservices

Application de gestion des offres d'emploi basée sur une architecture microservices.

## Architecture

```
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Frontend   │───▶│  API Gateway │───▶│   MS Offres     │
│  React.js   │    │  Port 8093   │    │ Spring Boot 8081│
│  Port 3000  │    │  + Keycloak  │    │  MySQL          │
└─────────────┘    │  + Swagger   │    └────────┬────────┘
                   └──────┬───────┘             │ Feign
                          │              ┌───────▼────────┐
                          └─────────────▶│  MS Candidats  │
                                         │ Node.js 8082   │
                                         │  MongoDB       │
                                         └────────────────┘
       ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
       │ Eureka Server│  │Config Server │  │   RabbitMQ   │
       │  Port 8761   │  │  Port 8888   │  │  Port 5672   │
       └──────────────┘  └──────────────┘  └──────────────┘
       ┌──────────────┐
       │  Keycloak    │
       │  Port 8080   │
       └──────────────┘
```

## Services

| Service | Technologie | Port | Base de données |
|---|---|---|---|
| MS Offres | Spring Boot 3.x | 8081 | MySQL 8.0 |
| MS Candidats | Node.js 22 + Express | 8082 | MongoDB 6.0 |
| API Gateway | Spring Cloud Gateway MVC | 8093 | — |
| Eureka Server | Spring Cloud Netflix | 8761 | — |
| Config Server | Spring Cloud Config | 8888 | — |
| Keycloak | Keycloak 24 | 8080 | — |
| RabbitMQ | RabbitMQ 3 | 5672/15672 | — |

## Prérequis

- Docker Desktop
- Node.js 22+
- Git

## Lancement

```bash
# 1. Cloner le projet
git clone <repo-url>
cd projetmicroservice

# 2. Démarrer tous les services Docker
docker-compose up -d

# 3. Attendre ~60 secondes que tous les services démarrent

# 4. Lancer le frontend
cd frontend
npm install
npm start
```

L'application est accessible sur **http://localhost:3000**

## Comptes de test Keycloak

| Rôle | Username | Password | Permissions |
|---|---|---|---|
| Recruteur | `recruteur1` | `password` | Créer/Modifier/Supprimer des offres |
| Candidat | `candidat1` | `password` | Postuler aux offres, voir ses candidatures |

## Interfaces disponibles

| Interface | URL |
|---|---|
| Application Frontend | http://localhost:3000 |
| **Swagger UI centralisé** | **http://localhost:8093/swagger-ui.html** |
| Eureka Dashboard | http://localhost:8761 |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |
| Keycloak Admin | http://localhost:8080 (admin/admin) |

## Sécurité — Keycloak

- **Realm** : `JobBoardRealm`
- **Client** : `jobboard-frontend` (public)
- **Rôles** : `recruteur`, `candidat`
- **JWT** validé au niveau de l'API Gateway

### Règles d'accès

| Endpoint | Méthode | Rôle requis |
|---|---|---|
| `/offres` | GET | Public |
| `/candidats` | GET | Public |
| `/offres` | POST | recruteur |
| `/offres/{id}` | PUT / DELETE | recruteur |
| `/candidats` | POST | candidat |

## Communication entre Microservices

### Synchrone — Feign Client

MS Offres appelle MS Candidats via Feign (HTTP):

- `GET /offres/{id}/candidats` → récupère les candidats d'une offre
- `GET /offres/candidat/{candidatId}` → récupère les détails d'un candidat

### Asynchrone — RabbitMQ

| Événement | Queue | Producteur | Consommateur |
|---|---|---|---|
| Nouvelle offre créée | `queue.offre.nouvelle` | MS Offres | MS Candidats |
| Offre supprimée | `queue.offre.supprimee` | MS Offres | MS Candidats |

## API Endpoints

### MS Offres (via Gateway :8093)

| Méthode | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/offres` | Liste toutes les offres | Public |
| GET | `/offres/{id}` | Détails d'une offre | Public |
| POST | `/offres` | Créer une offre | recruteur |
| PUT | `/offres/{id}` | Modifier une offre | recruteur |
| DELETE | `/offres/{id}` | Supprimer une offre | recruteur |
| GET | `/offres/actives` | Offres actives | Public |
| GET | `/offres/{id}/candidats` | Candidats d'une offre (Feign) | Public |
| GET | `/offres/candidat/{id}` | Détails d'un candidat (Feign) | Public |

### MS Candidats (via Gateway :8093)

| Méthode | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/candidats` | Liste tous les candidats | Public |
| GET | `/candidats/{id}` | Détails d'un candidat | Public |
| POST | `/candidats` | Postuler à une offre | candidat |
| PUT | `/candidats/{id}` | Modifier une candidature | Authentifié |
| DELETE | `/candidats/{id}` | Supprimer une candidature | Authentifié |

## Valeurs ajoutées

- **Swagger UI centralisé** au niveau du Gateway : `http://localhost:8093/swagger-ui.html`
- **Notifications temps réel** (polling 8s) pour recruteur et candidat
- **Upload CV** PDF/image : stockage base64 en MongoDB
- **Déduplication candidatures** : un candidat ne peut postuler qu'une fois par offre
- **Vue candidats par offre** : le recruteur filtre les candidats par offre
- **Téléchargement CV** : le recruteur peut télécharger le CV d'un candidat

## Structure du projet

```
projetmicroservice/
├── ms-offres/          # Spring Boot + MySQL + Feign + RabbitMQ producer
├── ms-candidats/       # Node.js + MongoDB + RabbitMQ consumer
├── api-gateway/        # Spring Cloud Gateway + Keycloak JWT + Swagger
├── eureka-server/      # Service Discovery
├── config-server/      # Configuration centralisée
├── keycloak/           # Realm auto-importé (recruteur1, candidat1)
├── frontend/           # React.js + Keycloak-js
└── docker-compose.yml  # Orchestration complète
```
