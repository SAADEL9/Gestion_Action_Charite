# Gestion Action Charite

`Gestion Action Charite` is a Spring Boot web application for managing charity initiatives. It combines a public-facing website, an authenticated donor space, and administration tools for organisations and platform admins.

The application lets users browse charity actions, register, participate in actions, and make donations. Organisation admins can create and manage organisations and actions, while platform admins can validate organisations and monitor platform activity from a dashboard.

## Main features

- Public home page with featured charity actions, categories, organisations, and platform statistics
- User registration and login with Spring Security
- Role-based access control for `USER`, `ORG_ADMIN`, and `ADMIN`
- Organisation management with approval workflow
- Charity action management with draft, published, ongoing, and archived states
- Participation registration for authenticated users
- Donation tracking with status management
- Stripe payment flow for card donations
- PayPal payment flow for redirected checkout donations
- Image upload support for organisation assets and charity action galleries
- Admin dashboard with recent actions, recent donations, totals, and pending organisations
- Thymeleaf server-rendered UI plus a small REST API for organisations and action galleries

## Tech stack

- Java 17
- Spring Boot 3
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- Stripe Java SDK
- PayPal REST integration via `RestTemplate`

## Roles and access

- `USER`: can register, log in, browse actions, participate, and donate
- `ORG_ADMIN`: can manage their organisation and its charity actions, and access the dashboard
- `ADMIN`: can manage the full platform, approve or reject organisations, and view global dashboard data

Security rules are configured in [SecurityConfig.java](/C:/Users/samia/OneDrive/Bureau/Gestion_Action_Charite/src/main/java/ma/emsi/gestionactioncharite/config/SecurityConfig.java).

## Core business entities

- `User`: platform user with role, profile data, donations, and participations
- `Organisation`: charity organisation managed by an admin user and subject to approval
- `ActionCharite`: charity campaign or action linked to an organisation and a category
- `Categorie`: classification for charity actions
- `Don`: donation record with amount, payment method, transaction id, and status
- `Participation`: user enrolment in a charity action

## Project structure

```text
src/main/java/ma/emsi/gestionactioncharite
|- config          # security, Stripe, MVC static resource mapping
|- controller      # MVC controllers and payment flows
|- controller/api  # REST endpoints
|- dto             # dashboard and user DTOs
|- entity          # JPA entities and enums
|- repository      # Spring Data repositories
|- security        # custom user details
|- service         # business logic and integrations
`- view            # lightweight view models for the home page

src/main/resources
|- static          # CSS, JS, images
`- templates       # Thymeleaf templates
```

## Configuration

An example configuration is provided in [application.properties.example](/C:/Users/samia/OneDrive/Bureau/Gestion_Action_Charite/src/main/resources/application.properties.example).

Create `src/main/resources/application.properties` and configure:

- Spring application port
- PostgreSQL connection URL, username, and password
- JPA settings
- Multipart upload limits
- Stripe public and secret keys
- PayPal client id, client secret, mode, and currency

Example:

```properties
spring.application.name=GestionActionCharite
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/action_de_charite_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

stripe.public.key=pk_test_YOUR_STRIPE_PUBLIC_KEY
stripe.secret.key=sk_test_YOUR_STRIPE_SECRET_KEY

paypal.client-id=YOUR_PAYPAL_CLIENT_ID
paypal.client-secret=YOUR_PAYPAL_CLIENT_SECRET
paypal.mode=sandbox
paypal.currency=EUR
```

## Prerequisites

- Java 17 installed
- PostgreSQL running locally or remotely
- A database named `action_de_charite_db` or an equivalent configured in `application.properties`
- Stripe test or live keys
- PayPal sandbox or live credentials

## Running the project

1. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties`.
2. Update database and payment credentials.
3. Create the PostgreSQL database if it does not exist.
4. Run the application:

```powershell
.\mvnw.cmd spring-boot:run
```

By default, the application starts on `http://localhost:8081`.

## Build and test

Compile:

```powershell
.\mvnw.cmd -DskipTests compile
```

Run tests:

```powershell
.\mvnw.cmd test
```

## File uploads

- Uploaded files are stored under the local `uploads/` directory
- Static access is exposed through `/uploads/**`
- Organisation logos and cover images are uploaded from the MVC controllers
- Action gallery images are managed through REST endpoints

The upload resource mapping is configured in [WebMvcConfig.java](/C:/Users/samia/OneDrive/Bureau/Gestion_Action_Charite/src/main/java/ma/emsi/gestionactioncharite/config/WebMvcConfig.java).

## Main routes

### Public and authenticated UI

- `/` home page
- `/login` login page
- `/register` registration page
- `/organisations` organisation listing
- `/actions` charity action listing
- `/actions/{id}` charity action detail
- `/admin/dashboard` admin or organisation dashboard

### REST API

- `GET /api/organisations`
- `GET /api/organisations/me`
- `POST /api/organisations`
- `POST /api/organisations/{id}/approve`
- `POST /api/organisations/{id}/reject`
- `POST /api/actions/{id}/gallery`
- `DELETE /api/actions/gallery/{imageId}`
- `GET /api/actions/{id}/images`

## Payments

Two donation payment flows are implemented:

- Stripe: creates and confirms a `PaymentIntent` for card payments
- PayPal: creates an order, redirects the donor for approval, then captures the order on return

Donation records are persisted with payment method and status so the platform can track pending, confirmed, and failed payments.

## Notes

- The project uses `spring.jpa.hibernate.ddl-auto=update`, which is convenient for development but should be reviewed before production use.
- The current test suite is minimal and contains a basic application context test.
- `target/` and runtime uploads should not be treated as source code.
