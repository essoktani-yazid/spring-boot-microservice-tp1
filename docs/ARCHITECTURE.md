# Architecture du Système — bank-account-service

> Ce document décrit l'architecture technique, les choix de conception et les flux de données de **bank-account-service**.

---

## 1. Vue d'ensemble (High-Level Design)

L'application repose sur une **architecture microservice en couches (Layered Architecture)** et le pattern **Service-Repository**, enrichie d'une **stratégie multi-paradigmes d'exposition d'APIs**. Elle permet d'accéder aux mêmes ressources métier via trois canaux distincts et complémentaires :
1. **API REST Métier** basée sur des DTOs et un contrôleur Spring MVC standard.
2. **API Hypermédia (Spring Data REST / HAL)** générée automatiquement à partir des repositories JPA avec support de projections HATEOAS.
3. **API GraphQL** orientée graphe permettant des requêtes sur-mesure et la navigation fluide entre entités sans sur-récupération (*over-fetching*).

```mermaid
graph TD
    ClientWeb["Client Web / Mobile / Swagger / GraphiQL"]
    
    subgraph SpringBootApp["bank-account-service (Port 8081)"]
        subgraph APILayer["Couche Exposition (Web / API)"]
            RESTCtrl["AccountRestController (/api/bankAccounts)"]
            GraphQLCtrl["BankAccountGraphQLController (/graphql)"]
            DataRest["Spring Data REST (/bankAccounts, /customers)"]
            ErrResolver["CustomDataFetcherExceptionResolver"]
        end
        
        subgraph ServiceLayer["Couche Métier & Transformation"]
            AccService["AccountService / AccountServiceImpl (@Transactional)"]
            AccMapper["AccountMapper (DTO <-> Entité)"]
            DTOs["BankAccountRequestDTO / BankAccountResponseDTO"]
        end
        
        subgraph DataLayer["Couche Persistance (Spring Data JPA)"]
            BankRepo["BankAccountRepository"]
            CustRepo["CustomerRepository"]
            Proj["AccountProjection (Projection p1)"]
            Entities["Entités JPA (BankAccount, Customer, AccountType)"]
        end
    end
    
    subgraph DatabaseLayer["Base de Données"]
        H2DB[("H2 In-Memory Database (account-db)")]
    end

    ClientWeb -->|HTTP REST JSON| RESTCtrl
    ClientWeb -->|POST GraphQL / GraphiQL| GraphQLCtrl
    ClientWeb -->|HTTP HAL / HATEOAS| DataRest
    
    RESTCtrl --> AccService
    GraphQLCtrl --> AccService
    GraphQLCtrl --> BankRepo
    GraphQLCtrl --> CustRepo
    GraphQLCtrl -.-> ErrResolver
    DataRest --> BankRepo
    DataRest --> CustRepo
    DataRest -.-> Proj
    
    AccService --> AccMapper
    AccService --> BankRepo
    AccMapper -.-> DTOs
    BankRepo --> Entities
    CustRepo --> Entities
    
    BankRepo -->|JDBC / Hibernate| H2DB
    CustRepo -->|JDBC / Hibernate| H2DB
```

---

## 2. Composants Principaux

| Composant | Technologie | Rôle & Responsabilités |
| :--- | :--- | :--- |
| **Contrôleur REST** | `AccountRestController` (Spring Web MVC) | Expose les endpoints REST standards sur `/api/bankAccounts` en utilisant des DTOs pour isoler les entités internes. |
| **Contrôleur GraphQL** | `BankAccountGraphQLController` (Spring GraphQL) | Gère les queries (`accountsList`, `accountById`, `customers`) et mutations (`addAccount`, `updateAccount`, `deleteAccount`) définies dans le schéma SDL. |
| **Gestionnaire d'Erreurs GraphQL** | `CustomDataFetcherExceptionResolver` | Intercepte les exceptions d'exécution (ex: compte introuvable) et fournit un message d'erreur clair dans le payload GraphQL. |
| **Hypermédia & Projections** | Spring Data REST & `AccountProjection` | Expose automatiquement des endpoints HAL (`/bankAccounts`, `/customers`) avec navigation hypermédia et projection partielle `p1`. |
| **Couche Service** | `AccountService` & `AccountServiceImpl` | Implémente la logique métier transactionnelle (`@Transactional`), l'assignation d'identifiants UUID et l'orchestration des mappers. |
| **Couche Mapping** | `AccountMapper` | Assure la conversion bidirectionnelle découplée entre `BankAccountRequestDTO`, `BankAccount` et `BankAccountResponseDTO`. |
| **Accès aux Données** | `BankAccountRepository` & `CustomerRepository` | Interfaces Spring Data JPA fournissant les opérations CRUD, la pagination et les requêtes dérivées (ex: `findByType`). |
| **Base de Données** | H2 In-Memory (`account-db`) | Base de données relationnelle en mémoire pour le stockage rapide et les tests sans infrastructure externe lourde. |

---

## 3. Flux de données (Data Workflow)

### Flux 1 : Création d'un compte bancaire via l'API REST Métier

Le diagramme ci-dessous illustre le traitement complet d'une requête de création de compte bancaire :

```mermaid
sequenceDiagram
    autonumber
    actor Client as Client HTTP (Postman / Swagger UI)
    participant Ctrl as AccountRestController
    participant Service as AccountServiceImpl (@Transactional)
    participant Mapper as AccountMapper
    participant Repo as BankAccountRepository
    participant DB as Base de Données H2

    Client->>Ctrl: POST /api/bankAccounts (JSON BankAccountRequestDTO)
    Ctrl->>Service: addAccount(requestDTO)
    Service->>Mapper: fromRequestDTO(requestDTO)
    Mapper-->>Service: instance BankAccount
    Service->>Service: Génération UUID + Date courante
    Service->>Repo: save(bankAccount)
    Repo->>DB: INSERT INTO BANK_ACCOUNT (...)
    DB-->>Repo: Enregistrement persistant
    Repo-->>Service: savedBankAccount
    Service->>Mapper: fromBankAccount(savedBankAccount)
    Mapper-->>Service: BankAccountResponseDTO
    Service-->>Ctrl: BankAccountResponseDTO
    Ctrl-->>Client: HTTP 200 OK (JSON BankAccountResponseDTO)
```

### Flux 2 : Consultation avec projection via Spring Data REST

```mermaid
sequenceDiagram
    autonumber
    actor Client as Client API / Navigateur
    participant DataRest as Spring Data REST Engine
    participant Repo as BankAccountRepository
    participant Proj as AccountProjection (p1)
    participant DB as Base de Données H2

    Client->>DataRest: GET /bankAccounts?projection=p1
    DataRest->>Repo: findAll()
    Repo->>DB: SELECT id, type, balance FROM BANK_ACCOUNT
    DB-->>Repo: Données brutes
    Repo-->>DataRest: Entités persistées
    DataRest->>Proj: Application de l'interface projection 'p1'
    Proj-->>DataRest: Champs restreints (id, type, balance)
    DataRest-->>Client: Réponse HAL JSON avec liens hypermédias (_links)
```

### Flux 3 : Exécution d'une requête GraphQL avec relation Customer

```mermaid
sequenceDiagram
    autonumber
    actor Client as Client GraphiQL
    participant GQL as BankAccountGraphQLController
    participant CustRepo as CustomerRepository
    participant DB as Base de Données H2

    Client->>GQL: POST /graphql (Query { customers { id name bankAccounts { id balance } } })
    GQL->>CustRepo: findAll()
    CustRepo->>DB: SELECT * FROM CUSTOMER
    DB-->>CustRepo: Liste des clients
    CustRepo-->>GQL: Instances Customer
    Note over GQL,DB: Chargement relationnel des comptes associés (@OneToMany)
    GQL->>DB: SELECT * FROM BANK_ACCOUNT WHERE CUSTOMER_ID = ?
    DB-->>GQL: Liste des BankAccounts
    GQL-->>Client: Payload JSON { data: { customers: [...] } }
```

---

## 4. Modèle de données & Stockage

### Modèle Relationnel (Entités JPA)

Le système modélise deux entités principales reliées par une relation bidirectionnelle 1 à N :

```mermaid
erDiagram
    CUSTOMER ||--o{ BANK_ACCOUNT : "détient (1..N)"
    
    CUSTOMER {
        Long id PK "Généré automatiquement (IDENTITY)"
        String name "Nom du titulaire"
    }
    
    BANK_ACCOUNT {
        String id PK "UUID alphanumérique (36 car.)"
        Date createdAt "Date d'ouverture"
        Double balance "Solde du compte"
        String currency "Devise (ex: MAD, EUR, USD)"
        AccountType type "CURRENT_ACCOUNT ou SAVING_ACCOUNT"
        Long customer_id FK "Référence au propriétaire"
    }
```

### Détails des entités et relations

- **`Customer` :**
  - Identifiant unique numérique auto-incrémenté (`Long id`).
  - Nom du client (`String name`).
  - Liste de comptes associés (`List<BankAccount> bankAccounts`), annotée avec `@OneToMany(mappedBy = "customer")`. L'annotation `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` prévient la récursion infinie lors de la sérialisation JSON REST.
- **`BankAccount` :**
  - Identifiant UUID généré côté serveur (`String id`).
  - Date de création (`Date createdAt`).
  - Solde (`Double balance`) et devise (`String currency`).
  - Type de compte (`AccountType type` : `CURRENT_ACCOUNT` ou `SAVING_ACCOUNT`), persisté en base sous forme de chaîne de caractères (`@Enumerated(EnumType.STRING)`).
  - Relation plusieurs-à-un vers le client (`@ManyToOne Customer customer`).

### Initialisation automatique des données

Au démarrage de l'application, un bean `CommandLineRunner` initialise la base H2 avec un jeu de données de test :
- Création de 4 clients : `Mohamed`, `Yassine`, `Hanae`, `Imane`.
- Génération de 10 comptes bancaires pour chaque client avec des soldes aléatoires et une répartition mixte entre comptes courants et d'épargne en devise `MAD`.

---

## 5. Sécurité, Configuration & Déploiement

- **Configuration de l'environnement (`application.properties`) :**
  - Nom de l'application : `bank-account-service`
  - Port d'écoute : `8081`
  - Source de données : `jdbc:h2:mem:account-db` (in-memory)
  - Console H2 : accessible sur `/h2-console`
  - GraphiQL : activé sur `/graphiql`
- **Sécurité :**
  - Environnement de développement libre sans contrainte d'authentification pour faciliter l'exploration et les tests rapides des trois interfaces d'API.
- **Build et Exécution :**
  - Compilé avec **Java 17** et **Maven**.
  - Packaging d'un artefact JAR exécutable autonome :
    ```bash
    ./mvnw clean package
    java -jar target/bank-account-service-0.0.1-SNAPSHOT.jar
    ```

---
[⬅ Retour au README principal](../README.md)
