## Electronic Invoicing (Costa Rica) – Backend Backbone

This project is a **Spring Boot** backend that provides the backbone for **Costa Rica electronic invoicing (Factura Electrónica)**, built with:

- Spring Boot (`web`, `data-jpa`, `security`)
- MySQL
- JWT-based authentication

It models the core entities of Costa Rican e-invoicing and exposes secure APIs to create invoices, persist them, and (stub) submit them to **Hacienda**.

---

## Architecture Overview

### Layers

- **API layer** (`api` package)
  - REST controllers for:
    - Authentication (`/api/auth`)
    - Invoices (`/api/invoices`)
- **Service layer** (`service` package)
  - Business logic for:
    - Creating invoices and calculating totals
    - Submitting invoices to Hacienda (backbone only)
- **Domain/model layer** (`domain` package)
  - JPA entities that represent:
    - Invoices and invoice lines
    - Parties (emitter and receiver)
    - Hacienda submissions
    - Users and roles for security
- **Persistence layer** (`repository` package)
  - Spring Data `JpaRepository` interfaces for the domain entities.
- **Integration layer** (`hacienda` package)
  - Stubs for:
    - Getting an access token from Hacienda
    - Building the XML structure (`FacturaElectronica`)
    - Recording submissions to Hacienda
- **Security layer** (`security` package)
  - JWT token handling
  - Spring Security configuration
  - User details and authentication filter

### Package layout

- `com.example.electronicinvoicing`
  - `ElectronicInvoicingApplication` – Spring Boot entry point and initial admin seeding
  - `api` – controllers and DTOs
  - `service` – business services
  - `domain` – JPA entities and enums
  - `repository` – Spring Data repositories
  - `security` – JWT and security config
  - `hacienda` – Hacienda integration backbone

---

## Domain Model and Mapping

### Invoice (`domain.Invoice`)

Represents a **Factura Electrónica** at a high level.

- **Key fields:**
  - `id` – primary key (DB)
  - `clave` – Hacienda 50-digit key (currently optional / placeholder; to be generated per spec)
  - `consecutivo` – document consecutive number
  - `documentType` – type code (e.g. `01` for standard invoice)
  - `issueDateTime` – `OffsetDateTime` of issuance
  - `currency` – 3-letter currency (e.g. `CRC`, `USD`)
  - `conditionSale` – condition of sale code
  - `paymentMethod` – payment method code
- **Monetary totals:**
  - `totalAmount`
  - `totalTax`
  - `totalDiscount`
- **Status fields:**
  - `status` (`InvoiceStatus` enum)
    - `DRAFT`, `READY`, `SENT`, `ACCEPTED`, `REJECTED`, `ERROR`
  - `haciendaStatus` (`HaciendaStatus` enum)
    - `PENDING`, `PROCESSED`, `ERROR`
  - `haciendaMessage` – last message/description from Hacienda (text)
- **Relationships:**
  - `emitter` – `ManyToOne` to `Party` (emisor)
  - `receiver` – `ManyToOne` to `Party` (receptor)
  - `lines` – `OneToMany` list of `InvoiceLine`

### Invoice Line (`domain.InvoiceLine`)

Represents a line item in the invoice, aligned with **CABYS** and tax calculation.

- **Key fields:**
  - `id`
  - `invoice` – `ManyToOne` to `Invoice`
  - `cabysCode` – CABYS code (up to 13 digits)
  - `description`
  - `quantity`
  - `unitPrice`
  - `discount`
  - `taxRate` – percentage (e.g. `13` for 13 %)
  - `taxAmount`
  - `lineTotal` – subtotal + tax – discount

### Party (`domain.Party`)

Represents a **subject** in the invoice: either **emitter** or **receiver**.

- **Key fields:**
  - `id`
  - `type` (`PartyType` enum: `EMITTER`, `RECEIVER`)
  - `identificationType` – code for type of ID
  - `identificationNumber`
  - `name`
  - `commercialName`
  - `email`
  - `phone`
  - `province`, `canton`, `district`
  - `addressLine`

These map directly to the **Encabezado** (header) sections of the official Hacienda XML structure (Emisor/Receptor).

### Hacienda Submission (`domain.HaciendaSubmission`)

Records each attempt to submit an invoice to Hacienda.

- **Key fields:**
  - `id`
  - `invoice` – `ManyToOne` to `Invoice`
  - `submissionDate` – `OffsetDateTime`
  - `status` – free-text status (currently `HaciendaStatus` name; may be specialized later)
  - `haciendaKey` – key used in Hacienda communication (may overlap with `clave`)
  - `responseCode`
  - `responseMessage`
  - `rawResponse` – full raw response payload (XML/JSON/etc.)
  - `retryCount`

This entity is designed so you can later audit every interaction with Hacienda.

### Security Entities (`domain.User`, `domain.Role`)

Used for local application-level authentication and authorization.

- **User:**
  - `id`
  - `username`
  - `password` (BCrypt hash)
  - `enabled`
  - `roles` – `ManyToMany` set of `Role`
- **Role:**
  - `id`
  - `name` (`RoleName` enum)
    - `ROLE_ADMIN`
    - `ROLE_OPERATOR`
    - `ROLE_READONLY`

These are mapped to Spring Security `UserDetails` and used for securing APIs.

---

## Persistence Layer

Repositories (`repository` package) are standard Spring Data JPA interfaces:

- `InvoiceRepository` – basic CRUD + queries by status and date range
- `InvoiceLineRepository`
- `PartyRepository` – including queries by `PartyType`
- `HaciendaSubmissionRepository`
- `UserRepository` – find by username, existence checks
- `RoleRepository` – find by `RoleName`

The default dev configuration (`application-dev.properties`) uses:

- `spring.jpa.hibernate.ddl-auto=update` – auto-creates/updates schema
- Logging of SQL statements for easier debugging

---

## Hacienda Integration Backbone

The goal of this project is to **prepare** the backbone for Hacienda integration, not to fully implement it yet.

### HaciendaAuthClient

`hacienda.HaciendaAuthClient` is responsible for obtaining an **access token** from Hacienda’s Identity Provider.

- Configured via properties:
  - `hacienda.auth.url`
  - `hacienda.client.id`
  - `hacienda.username`
  - `hacienda.password`
- Current behavior:
  - Logs the intent to request a token.
  - Returns a **stub** token string: `"stub-access-token"`.
  - Ready to be extended with a real HTTP client and response parsing.

### FacturaElectronicaXmlBuilder

`hacienda.FacturaElectronicaXmlBuilder` builds the XML representation of an `Invoice`.

- Current behavior:
  - Logs the XML building step.
  - Returns a **minimal placeholder XML**:
    - `<FacturaElectronica>`
      - `<Clave>` – from `invoice.clave`
      - `<NumeroConsecutivo>` – from `invoice.consecutivo`
    - `</FacturaElectronica>`
- Future expectations:
  - Align with **Hacienda v4.4** XSD structure for:
    - Headers (`Encabezado`)
    - Details (`DetalleServicio`)
    - Totals (`ResumenFactura`)
    - References (`InformacionReferencia`)

### HaciendaSubmissionService

`hacienda.HaciendaSubmissionService` orchestrates submissions:

- Steps:
  1. Fetch access token from `HaciendaAuthClient`.
  2. Build XML from `FacturaElectronicaXmlBuilder`.
  3. (Future) Send XML to Hacienda’s recepción endpoint.
  4. Persist a `HaciendaSubmission` row with:
     - Submission time
     - Status (`PENDING` for now)
     - Placeholder raw response text
- Current behavior:
  - Does **not** perform a real HTTP call, but **persists a submission record** so the flow and data are fully traceable.

---

## Security and Authentication

### JWT Setup

- `security.JwtTokenProvider`
  - Generates JWT tokens for authenticated users.
  - Reads:
    - `security.jwt.secret`
    - `security.jwt.expiration-seconds`
- `security.JwtAuthenticationFilter`
  - Extracts the `Authorization: Bearer <token>` header.
  - Validates token using `JwtTokenProvider`.
  - Loads user details using `CustomUserDetailsService`.
  - Populates Spring Security’s `SecurityContext`.
- `security.CustomUserDetailsService`
  - Adapts our `User` entity into a `UserDetails` object.
- `security.SecurityConfig`
  - Disables sessions (stateless, JWT-based).
  - Exposes a `SecurityFilterChain`:
    - Permits:
      - `/api/auth/**`
      - `/actuator/health`
      - `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`
    - Secures:
      - `/api/invoices/**` – requires roles `ADMIN` or `OPERATOR`
    - Adds the `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
  - Configures `BCryptPasswordEncoder` and `AuthenticationManager`.

### Default Admin User

`ElectronicInvoicingApplication` includes a `CommandLineRunner` that:

- Checks if a user with username **`admin`** exists.
- If not:
  - Creates role `ROLE_ADMIN` (if it does not already exist).
  - Creates a user:
    - username: `admin`
    - password: `admin123` (BCrypt-encoded)
    - roles: `[ROLE_ADMIN]`

This gives you an immediate way to log in and test the secured APIs.

---

## Services and Business Logic

### InvoiceService

Key responsibilities:

- Build and persist:
  - `Party` objects for emitter and receiver from `PartyRequest`.
  - `Invoice` and associated `InvoiceLine` entities from `CreateInvoiceRequest`.
- Calculate:
  - Per-line subtotal, tax, and line total.
  - Aggregate totals (`totalAmount`, `totalTax`, `totalDiscount`).
- Set metadata:
  - `issueDateTime` as current timestamp.
  - `status` initially as `DRAFT`.
- Provide:
  - `createInvoice(CreateInvoiceRequest)`
  - `getInvoice(Long id)`
  - `listInvoices()`

Return type is `InvoiceResponse`, which exposes high-level information (IDs, status, Hacienda status, total amount).

### InvoiceSubmissionService

Key responsibilities:

- Given an `invoiceId`:
  - Load the invoice.
  - Set:
    - `status = SENT`
    - `haciendaStatus = PENDING`
  - Call `HaciendaSubmissionService.submitInvoice(invoice)` to record the submission.

This is the main entry point for the “send to Hacienda” flow.

---

## API Endpoints and Usage

### Authentication

**Endpoint:** `POST /api/auth/login`

- Request body:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

- Response:

```json
{
  "token": "<jwt-token>"
}
```

- Use the token in subsequent requests:

```http
Authorization: Bearer <jwt-token>
```

### Invoices

All invoice endpoints require a valid JWT and appropriate role (`ADMIN` or `OPERATOR`).

#### Create Invoice

**Endpoint:** `POST /api/invoices`

- Example request:

```json
{
  "documentType": "01",
  "currency": "CRC",
  "conditionSale": "01",
  "paymentMethod": "01",
  "emitter": {
    "identificationType": "01",
    "identificationNumber": "123456789",
    "name": "Emisor SA"
  },
  "receiver": {
    "identificationType": "01",
    "identificationNumber": "987654321",
    "name": "Receptor SA"
  },
  "lines": [
    {
      "cabysCode": "1234567890123",
      "description": "Servicio",
      "quantity": 1,
      "unitPrice": 10,
      "discount": 0,
      "taxRate": 13
    }
  ]
}
```

- Example response:

```json
{
  "id": 1,
  "clave": null,
  "consecutivo": null,
  "status": "DRAFT",
  "haciendaStatus": "PENDING",
  "haciendaMessage": null,
  "totalAmount": 11.3
}
```

> Note: `clave` and `consecutivo` are currently not generated automatically; you can extend the service layer to handle this according to Hacienda’s v4.4 rules.

#### Get Invoice by ID

**Endpoint:** `GET /api/invoices/{id}`

- Returns `InvoiceResponse` with current status and totals.

#### List Invoices

**Endpoint:** `GET /api/invoices`

- Returns a list of `InvoiceResponse` objects.

#### Submit Invoice to Hacienda (Backbone)

**Endpoint:** `POST /api/invoices/{id}/submit`

- Behavior:
  - Marks the invoice as:
    - `status = SENT`
    - `haciendaStatus = PENDING`
  - Builds a minimal XML representation.
  - Calls `HaciendaAuthClient` to get a stub token.
  - Persists a `HaciendaSubmission` record with stub response data.
  - Returns **HTTP 202 Accepted**.

This endpoint is the main integration point where you will later:

- Plug in the real Hacienda recepción API URLs.
- Add digital signing and base64 encoding of the XML.
- Implement status polling and proper `ACCEPTED`/`REJECTED` updates.

---

## Configuration and Environment

### Profiles

- `application.properties`
  - Sets the app name and active profile:
    - `spring.application.name=electronic-invoicing`
    - `spring.profiles.active=dev`
- `application-dev.properties`
  - Assumes local MySQL:
    - `jdbc:mysql://localhost:3306/electronic_invoicing`
  - Uses env vars:
    - `EINV_DB_USERNAME`
    - `EINV_DB_PASSWORD`
    - `JWT_SECRET`
  - Sets:
    - `spring.jpa.hibernate.ddl-auto=update`
    - SQL logging enabled.
  - Provides default Hacienda URLs and credentials as overridable properties.
- `application-prod.properties`
  - Relies entirely on environment variables for:
    - `EINV_DB_URL`, `EINV_DB_USERNAME`, `EINV_DB_PASSWORD`
    - `HACIENDA_AUTH_URL`, `HACIENDA_API_URL`
    - `HACIENDA_CLIENT_ID`, `HACIENDA_USERNAME`, `HACIENDA_PASSWORD`
    - `JWT_SECRET`
  - Disables schema auto-update and hides error details.

### Environment Variables (Dev)

Set at least the following before running locally:

- `EINV_DB_USERNAME` – MySQL username
- `EINV_DB_PASSWORD` – MySQL password
- `JWT_SECRET` – sufficiently long random string for HMAC signing (aim for 32+ characters)

Optionally override:

- `HACIENDA_AUTH_URL`
- `HACIENDA_API_URL`
- `HACIENDA_CLIENT_ID`
- `HACIENDA_USERNAME`
- `HACIENDA_PASSWORD`

---

## Running the Project

### Prerequisites

- Java (JDK) – project is configured for Java 21 by default. If your environment only supports Java 17, update `<java.version>` in `pom.xml` accordingly.
- MySQL running and a database created:

```sql
CREATE DATABASE electronic_invoicing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Commands

From the project directory:

```bash
./mvnw spring-boot:run
```

Or, on Windows PowerShell:

```powershell
cd "C:\Users\sgtjo\OneDrive\Desktop\Projects\GitRepos\electronic-invoicing\electronic-invoicing"
./mvnw spring-boot:run
```

The app will start on `http://localhost:8080`.

---

## Testing

### Unit / Integration Tests

An initial test exists:

- `InvoiceServiceTest`
  - Uses `@DataJpaTest` and `@Import(InvoiceService.class)`.
  - Verifies:
    - Creating an invoice also persists emitter and receiver parties.

You can extend this with:

- Tests for:
  - XML builder behavior.
  - Security configuration (access control).
  - Hacienda submission status transitions.

Run tests with:

```bash
./mvnw test
```

---

## Next Steps / Extension Points

- **Clave and Consecutivo generation**
  - Implement a dedicated service to generate values according to **Hacienda v4.4** rules.
- **Full XML implementation**
  - Map all `Invoice`, `InvoiceLine`, and `Party` fields to the official XML schema (Encabezado, DetalleServicio, ResumenFactura, etc.).
  - Add XSD validation against Hacienda’s official schemas.
- **Real Hacienda communication**
  - Use `WebClient` or `RestTemplate` to:
    - Authenticate with the IDP.
    - Submit signed and base64-encoded XML to the recepción endpoint.
    - Poll for status and update invoices to `ACCEPTED` or `REJECTED`.
- **UI / Frontend**
  - Add a web UI or integrate with other systems to create/view invoices via these APIs.

This README describes how the project is structured, how data is mapped for Costa Rica’s electronic invoicing, and how the system is expected to behave when used as the backend backbone for issuing and managing electronic invoices.

