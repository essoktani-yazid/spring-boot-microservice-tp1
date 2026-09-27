# Référence API — bank-account-service

> Documentation technique exhaustive des endpoints exposés par le microservice **bank-account-service**. Le service propose trois interfaces complémentaires : l'API REST Métier (avec DTOs), l'API Hypermédia Spring Data REST et l'API GraphQL.

- **URL de base (Développement) :** `http://localhost:8081`
- **Format d'échange :** `application/json` (REST / Spring Data REST) et `application/graphql-response+json` (GraphQL)
- **Documentation interactive Swagger / OpenAPI :** `http://localhost:8081/swagger-ui/index.html`
- **Interface interactive GraphiQL :** `http://localhost:8081/graphiql`
- **Console H2 Database :** `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:mem:account-db`, User: `sa`, Mot de passe: *vide*)

---

## 1. API REST Métier (`/api/bankAccounts`)

Cette interface est gérée par `AccountRestController` et applique la séparation stricte DTO / Entité via `AccountService`.

### 1.1 Récupérer tous les comptes bancaires

Retourne la liste complète des comptes bancaires sous forme de DTOs.

- **URL :** `/api/bankAccounts`
- **Méthode :** `GET`
- **Authentification :** Aucune (mode développement)

#### Exemple de Réponse (`200 OK`)

```json
[
  {
    "id": "7bf3b0fa-ea68-45b7-a3a8-42283a0058b2",
    "createdAt": "2026-10-24T22:30:15.123+00:00",
    "balance": 45800.5,
    "currency": "MAD",
    "type": "CURRENT_ACCOUNT"
  },
  {
    "id": "e331cbf4-9c59-4592-8054-9d51829e7c51",
    "createdAt": "2026-10-24T22:30:15.125+00:00",
    "balance": 120500.0,
    "currency": "MAD",
    "type": "SAVING_ACCOUNT"
  }
]
```

---

### 1.2 Récupérer un compte par son identifiant

- **URL :** `/api/bankAccounts/{id}`
- **Méthode :** `GET`
- **Paramètre d'URL :** `id` (UUID du compte bancaire, ex: `7bf3b0fa-ea68-45b7-a3a8-42283a0058b2`)

#### Exemple de Réponse (`200 OK`)

```json
{
  "id": "7bf3b0fa-ea68-45b7-a3a8-42283a0058b2",
  "createdAt": "2026-10-24T22:30:15.123+00:00",
  "balance": 45800.5,
  "currency": "MAD",
  "type": "CURRENT_ACCOUNT"
}
```

#### Réponse si introuvable (`500 Internal Server Error` ou exception non gérée)

```json
{
  "timestamp": "2026-10-24T22:35:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Account 00000000-0000-0000-0000-000000000000 Not found",
  "path": "/api/bankAccounts/00000000-0000-0000-0000-000000000000"
}
```

---

### 1.3 Créer un nouveau compte bancaire

Génère automatiquement un identifiant UUID et la date de création côté serveur.

- **URL :** `/api/bankAccounts`
- **Méthode :** `POST`
- **En-tête :** `Content-Type: application/json`

#### Corps de la requête (Request Body - `BankAccountRequestDTO`)

```json
{
  "balance": 75000.0,
  "currency": "MAD",
  "type": "CURRENT_ACCOUNT"
}
```

> **Types acceptés pour le champ `type` :** `CURRENT_ACCOUNT`, `SAVING_ACCOUNT`.

#### Exemple de Réponse (`200 OK`)

```json
{
  "id": "f5e92c42-bb44-4691-88c9-026d36ef554a",
  "createdAt": "2026-10-24T22:45:10.820+00:00",
  "balance": 75000.0,
  "currency": "MAD",
  "type": "CURRENT_ACCOUNT"
}
```

---

### 1.4 Mettre à jour un compte existant

Met à jour les attributs solde, devise et type d'un compte identifié par son UUID.

- **URL :** `/api/bankAccounts/{id}`
- **Méthode :** `PUT`
- **En-tête :** `Content-Type: application/json`

#### Corps de la requête (Request Body - `BankAccountRequestDTO`)

```json
{
  "balance": 82000.0,
  "currency": "EUR",
  "type": "SAVING_ACCOUNT"
}
```

#### Exemple de Réponse (`200 OK`)

```json
{
  "id": "f5e92c42-bb44-4691-88c9-026d36ef554a",
  "createdAt": "2026-10-24T22:45:10.820+00:00",
  "balance": 82000.0,
  "currency": "EUR",
  "type": "SAVING_ACCOUNT"
}
```

---

### 1.5 Supprimer un compte bancaire

- **URL :** `/api/bankAccounts/{id}`
- **Méthode :** `DELETE`
- **Réponse :** `200 OK` (corps vide)

---

## 2. API Hypermédia Spring Data REST (HAL / HATEOAS)

Cette interface expose directement les entités avec pagination, liens de découverte HATEOAS et projections.

### 2.1 Endpoints Hypermédias de base

| Méthode | URL | Description |
| :--- | :--- | :--- |
| `GET` | `/bankAccounts` | Liste paginée des comptes avec liens d'accès individuels (`_links.self`). |
| `GET` | `/bankAccounts/{id}` | Ressource d'un compte avec navigation vers le client propriétaire (`_links.customer`). |
| `GET` | `/customers` | Liste paginée des clients enregistrés. |
| `GET` | `/customers/{id}` | Détails d'un client spécifique. |
| `GET` | `/customers/{id}/bankAccounts` | Navigation vers les comptes bancaires détenus par ce client. |

#### Exemple de réponse HAL (`GET /bankAccounts`)

```json
{
  "_embedded": {
    "bankAccounts": [
      {
        "createdAt": "2026-10-24T22:30:15.123+00:00",
        "balance": 45800.5,
        "currency": "MAD",
        "type": "CURRENT_ACCOUNT",
        "_links": {
          "self": {
            "href": "http://localhost:8081/bankAccounts/7bf3b0fa-ea68-45b7-a3a8-42283a0058b2"
          },
          "bankAccount": {
            "href": "http://localhost:8081/bankAccounts/7bf3b0fa-ea68-45b7-a3a8-42283a0058b2{?projection}",
            "templated": true
          },
          "customer": {
            "href": "http://localhost:8081/bankAccounts/7bf3b0fa-ea68-45b7-a3a8-42283a0058b2/customer"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8081/bankAccounts"
    },
    "profile": {
      "href": "http://localhost:8081/profile/bankAccounts"
    },
    "search": {
      "href": "http://localhost:8081/bankAccounts/search"
    }
  },
  "page": {
    "size": 20,
    "totalElements": 40,
    "totalPages": 2,
    "number": 0
  }
}
```

---

### 2.2 Projections Spring Data REST (`AccountProjection`)

L'interface de projection `p1` filtre la charge utile retournée pour ne contenir que `id`, `type` et `balance` :

- **URL :** `/bankAccounts?projection=p1`
- **Méthode :** `GET`

#### Exemple de réponse projetée :

```json
{
  "_embedded": {
    "bankAccounts": [
      {
        "id": "7bf3b0fa-ea68-45b7-a3a8-42283a0058b2",
        "type": "CURRENT_ACCOUNT",
        "balance": 45800.5,
        "_links": {
          "bankAccount": {
            "href": "http://localhost:8081/bankAccounts/7bf3b0fa-ea68-45b7-a3a8-42283a0058b2"
          }
        }
      }
    ]
  }
}
```

---

### 2.3 Recherche personnalisée (`@RestResource`)

Recherche de comptes par type via la méthode annotée dans `BankAccountRepository` :

- **URL :** `/bankAccounts/search/byType?t=SAVING_ACCOUNT`
- **Méthode :** `GET`
- **Paramètre requis :** `t` (`CURRENT_ACCOUNT` ou `SAVING_ACCOUNT`)

---

## 3. API GraphQL (`/graphql`)

L'API GraphQL permet d'exécuter des requêtes dynamiques et des mutations via l'endpoint unique `/graphql`.

- **Endpoint :** `http://localhost:8081/graphql`
- **Méthode HTTP :** `POST`
- **En-tête requis :** `Content-Type: application/json`

### 3.1 Schéma SDL (`schema.graphqls`)

```graphql
type Query {
    accountsList : [BankAccount],
    accountById (id:String) : BankAccount
    customers: [Customer]
}

type Mutation {
    addAccount(bankAccount : BankAccountDTO) : BankAccount
    updateAccount(id:String, bankAccount:BankAccountDTO) : BankAccount
    deleteAccount(id:String) : String
}

type Customer {
    id: ID,
    name: String,
    bankAccounts: [BankAccount]
}

type BankAccount {
    id : String,
    createdAt : String,
    balance : Float,
    currency : String,
    type : String,
    customer: Customer
}

input BankAccountDTO {
    balance : Float,
    currency : String,
    type : String
}
```

---

### 3.2 Requêtes GraphQL (Queries)

#### Requête 1 : Liste de tous les comptes avec informations du client

```graphql
query {
  accountsList {
    id
    createdAt
    balance
    currency
    type
    customer {
      id
      name
    }
  }
}
```

#### Requête 2 : Recherche d'un compte par son identifiant

```graphql
query {
  accountById(id: "7bf3b0fa-ea68-45b7-a3a8-42283a0058b2") {
    id
    balance
    currency
    type
    createdAt
  }
}
```

#### Requête 3 : Liste des clients avec leurs comptes bancaires associés

```graphql
query {
  customers {
    id
    name
    bankAccounts {
      id
      balance
      type
      currency
    }
  }
}
```

---

### 3.3 Mutations GraphQL

#### Mutation 1 : Ajouter un nouveau compte (`addAccount`)

```graphql
mutation {
  addAccount(bankAccount: {
    balance: 95000.0,
    type: "SAVING_ACCOUNT",
    currency: "MAD"
  }) {
    id
    createdAt
    balance
    type
    currency
  }
}
```

#### Mutation 2 : Modifier un compte existant (`updateAccount`)

```graphql
mutation {
  updateAccount(
    id: "7bf3b0fa-ea68-45b7-a3a8-42283a0058b2",
    bankAccount: {
      balance: 110000.0,
      type: "SAVING_ACCOUNT",
      currency: "MAD"
    }
  ) {
    id
    balance
    type
  }
}
```

#### Mutation 3 : Supprimer un compte (`deleteAccount`)

```graphql
mutation {
  deleteAccount(id: "7bf3b0fa-ea68-45b7-a3a8-42283a0058b2")
}
```

---

### 3.4 Gestion personnalisée des erreurs GraphQL

Le composant `CustomDataFetcherExceptionResolver` intercepte les exceptions et produit une réponse GraphQL formattée avec un message explicite :

```json
{
  "errors": [
    {
      "message": "Account invalid-id not found"
    }
  ],
  "data": {
    "accountById": null
  }
}
```

---

## 4. Codes de statut HTTP & Réponses

| Code HTTP | Signification | Description |
| :--- | :--- | :--- |
| `200 OK` | Succès | Requête traitée avec succès (GET, POST de création REST/GraphQL, PUT, DELETE). |
| `400 Bad Request` | Requête invalide | Syntaxe JSON invalide ou paramètre manquant. |
| `404 Not Found` | Introuvable | Ressource Spring Data REST non trouvée sur l'URL spécifiée. |
| `500 Internal Error` | Erreur serveur | Identifiant de compte introuvable ou erreur de traitement métier non gérée. |

---
[⬅ Retour au README principal](../README.md)
