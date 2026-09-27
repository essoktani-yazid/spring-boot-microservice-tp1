<a id="readme-top"></a>

<!-- =========================================================================
  DOCUMENTATION TECHNIQUE D'INGENIERIE LOGICIELLE
  Master Systemes Distribues et Intelligence Artificielle (SDIA) - ENSET Mohammedia
  Auteur : Yazid ESSOKTANI (http://essoktani.tech/)
  Projet : Bank Account Microservice (Activite Pratique N°2)
========================================================================= -->

<div align="center">

  <!-- Banniere superieure technique (capsule-render) -->
 <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=0,2,9,20&height=180&section=header&text=BANK%20ACCOUNT%20MICROSERVICE&fontSize=40&fontAlignY=38&desc=Activité%20Pratique%20N°1%20:%20REST,%20GraphQL,%20Spring%20Data%20&%20OpenAPI&descSize=16&descAlignY=62&fontColor=ffffff" alt="Bannière du Projet" width="100%" />
  <br />

  <!-- Metriques et Statuts du Depot -->
  <p>
    <a href="https://github.com/essoktani/bank-account-service">
      <img src="https://img.shields.io/badge/Depot-bank--account--service-24292e?style=flat-square&logo=github" alt="Depot GitHub" />
    </a>
    <a href="LICENSE">
      <img src="https://img.shields.io/badge/Licence-MIT-24292e?style=flat-square" alt="Licence" />
    </a>
    <img src="https://img.shields.io/badge/Statut-Livrable%20Valide-success?style=flat-square" alt="Statut" />
  </p>

  <!-- Liens Professionnels & Identite Auteur -->
  <p>
    <a href="http://essoktani.tech/">
      <img src="https://img.shields.io/badge/Portfolio-essoktani.tech-05122A?style=flat-square&logo=googlechrome&logoColor=white" alt="Portfolio" />
    </a>
    <a href="https://www.linkedin.com/in/yazid-essoktani-337549251/">
      <img src="https://img.shields.io/badge/LinkedIn-Yazid_ESSOKTANI-0A66C2?style=flat-square&logo=linkedin&logoColor=white" alt="LinkedIn" />
    </a>
    <a href="mailto:essoktani.yazid@gmail.com">
      <img src="https://img.shields.io/badge/Email-essoktani.yazid@gmail.com-D14836?style=flat-square&logo=gmail&logoColor=white" alt="Email" />
    </a>
  </p>

  <!-- Technologies Cles -->
  <p>
    <img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17" />
    <img src="https://img.shields.io/badge/Spring_Boot-3.x%20%2F%204.x-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot" />
    <img src="https://img.shields.io/badge/Spring_Data-JPA%20%26%20REST-6DB33F?style=flat-square&logo=spring&logoColor=white" alt="Spring Data" />
    <img src="https://img.shields.io/badge/GraphQL-Spring_GraphQL-E10098?style=flat-square&logo=graphql&logoColor=white" alt="Spring GraphQL" />
    <img src="https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=flat-square&logo=swagger&logoColor=black" alt="Swagger" />
    <img src="https://img.shields.io/badge/H2-In--Memory_DB-003B57?style=flat-square" alt="H2 Database" />
  </p>

  <!-- Navigation Rapide -->
  <p>
    <a href="#1-presentation-du-tp">Presentation</a> |
    <a href="#2-demarrage-rapide">Demarrage</a> |
    <a href="#3-guide-visuel-et-captures-decran">Guide des Captures</a> |
    <a href="#4-architecture-simplifiee">Architecture</a> |
    <a href="#5-contexte-academique">Contexte</a>
  </p>

</div>

<br />

<!-- =========================================================================
  CADRE DE CONTEXTE ACADEMIQUE ET INSTITUTIONNEL
========================================================================= -->
<div align="center">

| Contexte Academique & Institutionnel | Identification du Projet |
| :--- | :--- |
| **Etablissement :** ENSET Mohammedia, Universite Hassan II | **Auteur :** [Yazid ESSOKTANI](https://www.linkedin.com/in/yazid-essoktani-337549251/) |
| **Filiere :** Master Systemes Distribues & Intelligence Artificielle (SDIA) | **Encadrant :** Prof. Mohamed YOUSSFI |
| **Departement :** Mathematiques et Informatique | **Annee Universitaire :** 2026 / 2027 |
| **Module d'Enseignement :** Systemes Distribues et DevOps | **Version :** `v1.0.0` |

</div>

---

## Sommaire

<details open>
<summary><b>Afficher / Masquer la table des matieres</b></summary>

1. [Presentation du TP](#1-presentation-du-tp)
2. [Demarrage Rapide](#2-demarrage-rapide)
   - [Prerequis et Lancement](#21-prerequis-et-lancement)
   - [Points d'Acces Utiles](#22-points-dacces-utiles)
3. [Guide Visuel et Captures d'Ecran](#3-guide-visuel-et-captures-decran)
   - [Etape 1 : Persistance et Base H2](#etape-1--persistance-et-base-h2)
   - [Etape 2 : Documentation et API REST (Swagger UI)](#etape-2--documentation-et-api-rest-swagger-ui)
   - [Etape 3 : Spring Data REST et Projections](#etape-3--spring-data-rest-et-projections)
   - [Etape 4 : API Declarative GraphQL (GraphiQL)](#etape-4--api-declarative-graphql-graphiql)
   - [Etape 5 : Gestion des Exceptions GraphQL](#etape-5--gestion-des-exceptions-graphql)
   - [Etape 6 : Relation Client et Comptes](#etape-6--relation-client-et-comptes)
4. [Architecture Simplifiee](#4-architecture-simplifiee)
5. [Contexte Academique](#5-contexte-academique)

</details>

---

# 1. Presentation du TP

Ce travail pratique porte sur l'implementation d'un microservice de gestion de comptes bancaires (`bank-account-service`). L'objectif principal est de maitriser et comparer trois modes d'exposition d'API :

- **API REST conventionnelle :** Architecture en couches avec `Controller`, `Service`, `DTO` (`BankAccountRequestDTO`, `BankAccountResponseDTO`) et `Mapper`.
- **Spring Data REST :** Exposition automatique des entites JPA avec gestion des projections (`AccountProjection`).
- **Spring for GraphQL :** Requetage dynamique, schema SDL (`schema.graphqls`), requetes (`Query`), mutations (`Mutation`) et resolution des erreurs.

<p align="right"><a href="#readme-top">Haut de page</a></p>

---

# 2. Demarrage Rapide

### 2.1 Prerequis et Lancement

- **Prerequis :** Java 17+, Maven 3.8+.
- **Compilation et execution :**

```bash
# Compilation du projet
mvn clean compile

# Lancement de l'application
mvn spring-boot:run
```

### 2.2 Points d'Acces Utiles

| Outil / Interface | URL par Defaut | Role |
| :--- | :--- | :--- |
| **Swagger UI** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Documentation et test interactif de l'API REST |
| **OpenAPI Docs** | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) | Specification brute OpenAPI en format JSON |
| **GraphiQL Console** | [http://localhost:8081/graphiql](http://localhost:8081/graphiql) | Editeur integre pour executer queries et mutations GraphQL |
| **Console H2** | [http://localhost:8081/h2-console](http://localhost:8081/h2-console) | Visualisation des tables et des donnees en memoire |
| **Endpoint REST** | [http://localhost:8081/api/bankAccounts](http://localhost:8081/api/bankAccounts) | Endpoints REST de base de l'application |
| **Spring Data REST** | [http://localhost:8081/bankAccounts](http://localhost:8081/bankAccounts) | Racine des ressources HATEOAS Spring Data REST |

<p align="right"><a href="#readme-top">Haut de page</a></p>

---

# 3. Guide Visuel et Captures d'Ecran

### Etape 1 : Persistance et Base H2

#### Capture 01 : Ecran de Connexion a la Console H2

![Console H2 - Connexion](screenshots/01_h2_console_login.png)

---

#### Capture 02 : Visualisation des Tables et Donnees dans H2

![Console H2 - Tables et Donnees](screenshots/02_h2_tables_data.png)

---

### Etape 2 : Documentation et API REST (Swagger UI)

#### Capture 03 : Vue Generale de l'Interface Swagger UI

![Swagger UI - Vue Generale](screenshots/03_swagger_ui_overview.png)

---

#### Capture 04 : Test de Creation de Compte via Swagger UI (POST)

![Swagger UI - Creation POST](screenshots/04_swagger_post_account.png)

---

#### Capture 05 : Test de Consultation des Comptes (GET)

![Swagger UI - Consultation GET](screenshots/05_swagger_get_accounts.png)

---

### Etape 3 : Spring Data REST et Projections

#### Capture 06 : Exposition HATEOAS Standard (/bankAccounts)

![Spring Data REST - HATEOAS](screenshots/06_spring_data_rest_hal.png)

---

#### Capture 07 : Test de la Projection p1 (AccountProjection)

![Spring Data REST - Projection p1](screenshots/07_spring_data_rest_projection.png)

---

### Etape 4 : API Declarative GraphQL (GraphiQL)

#### Capture 08 : Interface GraphiQL et Explorateur de Schema

![GraphiQL - Interface et Documentation](screenshots/08_graphiql_interface.png)

---

#### Capture 09 : Requete GraphQL Query (accountsList)

![GraphQL - Requete accountsList](screenshots/09_graphql_query_accounts.png)

---

#### Capture 10 : Requete GraphQL par Identifiant (bankAccountById)

![GraphQL - Requete par ID](screenshots/10_graphql_query_by_id.png)

---

#### Capture 11 : Mutation GraphQL d'Ajout de Compte (addAccount)

![GraphQL - Mutation addAccount](screenshots/11_graphql_mutation_add.png)

---

### Etape 5 : Gestion des Exceptions GraphQL

#### Capture 12 : Test de Resolution d'Erreur (Compte Inexistant)

![GraphQL - Gestion des Erreurs](screenshots/12_graphql_error_handling.png)

---

### Etape 6 : Relation Client et Comptes

#### Capture 13 : Consultation des Clients et de Leurs Comptes Associes

![GraphQL - Relation Customer BankAccounts](screenshots/13_customer_relation.png)

<p align="right"><a href="#readme-top">Haut de page</a></p>

---

# 4. Architecture Simplifiee

Le schema suivant resume de facon directe les differents flux de communication vers la couche de donnees :

```mermaid
flowchart TD
    Client["Client HTTP / Navigateur / Mobile"]

    subgraph Endpoints["Points d'Entree du Microservice"]
        REST["API REST Conventionnelle<br/>/api/bankAccounts"]
        DataREST["Spring Data REST & Projections<br/>/bankAccounts"]
        GQL["Spring for GraphQL<br/>/graphql"]
    end

    subgraph Metier["Couche Service & Metier"]
        Service["AccountService & AccountMapper<br/>(Gestion DTOs & Validation)"]
    end

    subgraph Persistance["Couche Acces aux Donnees"]
        Repo["Spring Data JPA<br/>(BankAccountRepository, CustomerRepository)"]
        H2[("Base de Donnees H2<br/>(In-Memory)")]
    end

    Client --> REST
    Client --> DataREST
    Client --> GQL

    REST --> Service
    GQL --> Service
    GQL --> Repo
    DataREST --> Repo

    Service --> Repo
    Repo --> H2

    style Client fill:#f6f8fa,stroke:#d0d7de,stroke-width:1px,color:#24292f;
    style Endpoints fill:#ffffff,stroke:#0969da,stroke-width:2px,color:#24292f;
    style Metier fill:#f6f8fa,stroke:#d0d7de,stroke-width:1px,color:#24292f;
    style Persistance fill:#f6f8fa,stroke:#1f883d,stroke-width:1px,color:#24292f;
```

<p align="right"><a href="#readme-top">Haut de page</a></p>
