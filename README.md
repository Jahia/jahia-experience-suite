# Jahia Experience Suite

A comprehensive Docker-based environment for Jahia development and testing. This project provides a complete Jahia digital experience platform stack with integrated authentication, personalization, content management, and search capabilities.

## Components

### Core Services

- **Jahia**: Enterprise digital experience platform (DXP) for content management
- **jCustomer (Unomi)**: Customer data platform for personalization and user profile management
- **MariaDB**: Database server for storing Jahia's content and user data
- **Elasticsearch**: Search and analytics engine supporting content search functionality
- **Kibana**: Data visualization dashboard for Elasticsearch

### Authentication & User Management

- **Keycloak**: Identity and access management solution
- **OpenLDAP**: Lightweight directory access protocol server for user authentication
- **phpLDAPadmin**: Web interface for LDAP management

### Infrastructure & Tools

- **Traefik**: Modern reverse proxy and load balancer
- **phpMyAdmin**: Web interface for database management
- **Nginx**: Web server for serving static provisioning files

## Architecture

The environment uses Docker Compose to orchestrate all services within a single network called `jahia-jcustomer-full-env`. Key integration points include:

1. **Authentication Flow**: Keycloak integrates with LDAP for user storage and provides SAML/OAuth for Jahia
2. **Personalization**: jCustomer collects user data and provides personalization services to Jahia
3. **Search**: Elasticsearch powers the Augmented Search functionality in Jahia
4. **Monitoring**: Kibana provides analytics dashboards for user behavior

## Getting Started

1. Clone this repository
2. Configure environment variables in `.env` file (start by copying `env.example` to `.env`)
3. Start the environment with `docker compose up -d`
4. Access services through their respective hostnames:
    - Jahia: http://jahia.localhost
    - jCustomer: http://jcustomer.localhost
    - Keycloak: http://keycloak.localhost
    - phpLDAPadmin: http://phpldapadmin.localhost
    - phpMyAdmin: http://phpmyadmin.localhost
    - Kibana: http://kibana.localhost

## Resource Allocations

The Docker Compose configuration specifies resource limits for containers:
- jCustomer: 2GB memory
- Elasticsearch: 2GB memory
- Kibana: 1GB memory
- Keycloak: 512MB memory
- phpLDAPadmin: 512MB memory
- phpMyAdmin: 256MB memory
- Lighter services (web, LDAP, reverse-proxy): 128MB memory each

## Configuration

The environment uses provisioning scripts to set up:
- Demo sites (Digitall, Luxe)
- LDAP authentication
- jCustomer integration
- Augmented Search
- Forms functionality

## Networking

All containers communicate through the `stack` network with subnet `172.16.1.0/24`.


## Authentication
The environment uses Keycloak for authentication, which is configured to use OpenLDAP as the user store. The Keycloak server is accessible at `http://keycloak.localhost/auth` and can be managed through its web interface.
### Keycloak Realm-IDP Configuration

The `realm-idp` in Keycloak serves as a central identity provider with both SAML and CAS integration for the Jahia Experience Suite environment.

#### SAML Configuration

The SAML integration allows secure identity federation between Keycloak and Jahia:

- **Entity ID**: `jahia-realm-idp`
- **Assertion Consumer Service**: Configured to receive assertions at Jahia's endpoint
- **Name ID Format**: `urn:oasis:names:tc:SAML:2.0:nameid-format:persistent`
- **Signature Algorithm**: RSA-SHA256
- **Attributes Mapped**:
    - `email`
    - `firstName`
    - `lastName`
    - `username`
    - `groups`

#### CAS Configuration

The Central Authentication Service (CAS) provides an alternative authentication method:

- **CAS Protocol**: Version 3.0
- **Service URL**: `http://jahia.localhost/cas`
- **Ticket Validity**: 5 minutes
- **Single Sign-Out**: Enabled

#### User Federation

The realm connects to OpenLDAP with these settings:

- **LDAP Connection**: Using the internal OpenLDAP service
- **User DN**: `ou=performance,dc=jahia,dc=com`
- **Username LDAP Attribute**: `cn`
- **RDN LDAP Attribute**: `uid`
- **UUID LDAP Attribute**: `entryUUID`

#### Client Scopes

Predefined scopes include:

- `jahia-profile`: Basic user information
- `jahia-roles`: User roles and permissions
- `offline_access`: For refresh tokens

#### Authentication Flow

The realm is configured with a custom authentication flow that includes:

1. Username/password form
2. LDAP authentication
3. OTP verification (optional)
4. User profile completion

This configuration enables seamless single sign-on across all services in the Jahia Experience Suite.

# Jahia Configuration : provisioning/provisioning.yaml

The `provisioning.yaml` file defines the automated setup and configuration of the Jahia Experience Suite environment. It follows a declarative approach to install components, configure services, and integrate various features.

## Core Components

1. **Demo Sites**:
    - **Digitall**: A comprehensive demo site with content
    - **Luxe**: A prepackaged website demo

2. **Modules & Bundles**:
    - **Content Management**: Base components, templates, skins
    - **User Interface**: Bootstrap3, Font Awesome
    - **Content Types**: Person, News, Events, Calendar, Press
    - **Interactive Features**: Bookmarks, Rating, Topstories

3. **Search & Analytics**:
    - Augmented Search with Elasticsearch integration
    - Custom search UI components
      - The setup includes:
        - Removing default search components
        - Adding Augmented Search UI components in the search result page
    - Configuration of search indexes
    - Trigger an indexation

4. **Authentication**:
    - LDAP integration for user management
    - SAML authentication valve
    - JCR auth provider
    - Authentication UI components
        - SAML Login button on home page
        - To test the CAS authentication, just hit http://keycloak.localhost/realms/realm-idp/protocol/cas/login?service=http%3A%2F%2Fjahia.localhost%2Fcms%2Frender%2Flive%2Ffr%2Fsites%2Fdigitall%2Fhome.html

5. **Customer Experience**:
    - jExperience for personalization
    - Forms capabilities (core, inputs, themes)
    - Kibana dashboards for analytics

## Integration Points

- **LDAP Configuration**: Multiple LDAP configurations for different sites
- **jCustomer Integration**: Configures connection to jCustomer (Unomi) with credentials
- **Kibana Integration**: Sets up dashboard access
- **Database Connector**: For storing and accessing structured data

## Automation

The provisioning uses several mechanisms:
- Script execution (`executeScript`) for complex operations
- Bundle installations from Maven repositories
- GraphQL operations for content manipulation
- Configuration file installations for service configuration
- Site imports from packaged ZIP archives

This configuration creates a fully functional Jahia environment with integrated authentication, personalization, search, and demo content.

# Traefik Configuration in the Jahia Experience Suite

Based on the provided information, Traefik serves as the modern reverse proxy and load balancer for the Jahia Experience Suite environment. Although the specific Docker label configurations aren't explicitly shown in the files provided, I can infer how Traefik is likely configured with Docker labels in this setup:

## General Traefik Configuration

Traefik is configured using Docker labels on each service container to:
- Define routing rules
- Enable TLS/SSL if needed
- Configure middleware
- Set load balancing options

## Service Routing

Each service in the stack is accessible through hostname-based routing:
- `jahia.localhost` → Jahia DXP
- `jcustomer.localhost` → jCustomer (Unomi)
- `keycloak.localhost` → Keycloak authentication server
- `phpldapadmin.localhost` → LDAP admin interface
- `phpmyadmin.localhost` → Database admin interface
- `kibana.localhost` → Elasticsearch visualization

## Docker Labels Pattern

The Docker Compose file would typically include labels like:

```yaml
services:
  jahia:
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.jahia.rule=Host(`jahia.localhost`)"
      - "traefik.http.services.jahia.loadbalancer.server.port=8080"
```

## Network Configuration

Traefik connects to the `stack` network (subnet `172.16.1.0/24`) to access all services in the environment, serving as the entry point for external requests.

## Load Balancing Features

Since the environment includes multiple services with varying resource requirements, Traefik likely handles:
- Health checking
- Request distribution
- Traffic prioritization based on service types

The Docker labels provide a declarative way to define how Traefik should direct incoming traffic to the appropriate containers without needing a separate configuration file.
