# Jahia Experience Suite

A comprehensive Docker-based environment for Jahia development and testing. This project provides a complete Jahia
digital experience platform stack with integrated authentication, personalization, content management, and search
capabilities.

## Table of Contents

- [Getting Started](#getting-started)
- [Components](#components)
- [Architecture](#architecture)
- [Resource Allocations](#resource-allocations)
- [Configuration](#configuration)
- [Networking](#networking)
- [TLS/SSL Configuration](#tlsssl-configuration)
- [Authentication](#authentication)
- [Jahia Configuration : provisioning/provisioning.yaml](#jahia-configuration--provisioningprovisioningyaml)
- [Traefik Configuration in the Jahia Experience Suite](#traefik-configuration-in-the-jahia-experience-suite)
- [Jahia Experience Suite Environment User](#jahia-experience-suite-environment-user)
- [CAS Link](#cas-link)
- [Clustering](#clustering)

## Getting Started

1. Clone this repository
2. Configure environment variables in `.env` file (start by copying `env.example` to `.env`)
3. Run `docker compose pull` to download the required images
4. (Optional) Generate self-signed SSL certificates
5. Run `docker compose build` to build custom images
3. Start the environment with `docker compose up -d`
4. Access services through their respective hostnames:
    - Jahia: https://jahia.localhost  
      - Dashboard: https://jahia.localhost/cms/login?redirect=/jahia/dashboard  (username: `root`, password: `root1234`)
      - Tools: https://jahia.localhost/cms/login?redirect=/tools  (username: `root`, password: `root1234`)
    - Luxe website: https://luxe.jahia.localhost  
      - SSO Authentication button is NOT included
    - Digitall website: https://digitall.jahia.localhost  
      - SSO authentication process using dedicated button `saml-button` (username: `monzos` password: `Monzo`) or (username: `ovansk`, password: `Ovans`)
      - Login Form based authentication will use ldap, same users but no keycloak redirect (username: `monzos` password: `Monzo`) or (username: `ovansk`, password: `Ovans`) 
    - jCustomer: https://jcustomer.localhost 
      - credentials: karaf:karaf
      - Sample curl: curl -u karaf:karaf -v https://jcustomer.localhost/cxs/scopes
    - Keycloak: https://keycloak.localhost
      - Be aware that keycloak always have a 'master' realm, so do not confuse it with the `realm-idp` realm
      - Access the Keycloak admin console at `https://keycloak.localhost/auth/admin` (username: `admin`, password: `admin`)
    - phpLDAPadmin: https://phpldapadmin.localhost
    - phpMyAdmin: https://phpmyadmin.localhost
      - Super user: (username: `root`, password: `mariadbP@55`)
      - Jahia user: (username: `jahia`, password: `jahia`)
    - Kibana: https://kibana.localhost
      - (username: `elastic`, password: `root1234`)
    - Traefik dashboard: https://localhost:9080/dashboard/
    - Mail SMTP4dev: https://mailserver.localhost
    - ElasticVue (using browser extension)
       - Locate the elasticsearch container IP: `docker inspect jahia-experience-suite-elasticsearch-1 | grep IPAddress`
       - config: `cluster name: jahia-es-cluster, uri: <elasticsearch-ip>:9200, username: elastic, password: root1234` 

## Components

### Core Services

- **Jahia**: Enterprise digital experience platform (DXP) for content management
- **jCustomer (Unomi)**: Customer data platform for personalization and user profile management
- **MariaDB**: Database server for storing Jahia's content and user data
- **Elasticsearch**: Search and analytics engine supporting content search functionality
- **Kibana**: Data visualization dashboard for Elasticsearch
- **SMTP4dev**: Local SMTP server for testing email functionality

### Authentication & User Management

- **Keycloak**: Identity and access management solution
- **OpenLDAP**: Lightweight directory access protocol server for user authentication
- **phpLDAPadmin**: Web interface for LDAP management

### Infrastructure & Tools

- **Traefik**: Modern reverse proxy and load balancer
- **phpMyAdmin**: Web interface for database management
- **Nginx**: Web server for serving static provisioning files

## Architecture

The environment uses Docker Compose to orchestrate all services within a single network called
`jahia-jcustomer-full-env`. Key integration points include:

1. **Authentication Flow**: Keycloak integrates with LDAP for user storage and provides SAML/OAuth for Jahia
2. **Personalization**: jCustomer collects user data and provides personalization services to Jahia
3. **Search**: Elasticsearch powers the Augmented Search functionality in Jahia
4. **Monitoring**: Kibana provides analytics dashboards for user behavior

## Resource Allocations

The Docker Compose configuration specifies resource limits for containers:

- jCustomer: 2GB memory
- Elasticsearch: 2GB memory
- Kibana: 1GB memory
- Keycloak: 512MB memory
- phpLDAPadmin: 512MB memory
- phpMyAdmin: 256MB memory
- SMTP4dev: 256MB memory
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

The environment uses Keycloak for authentication, which is configured to use OpenLDAP as the user store. The Keycloak
server is accessible at `http://keycloak.localhost/auth` and can be managed through its web interface.

### Keycloak Realm-IDP Configuration

The `realm-idp` in Keycloak serves as a central identity provider with both SAML and CAS integration for the Jahia
Experience Suite environment.

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

#### Authentication Flow

The realm is configured with a custom authentication flow that includes:

1. Username/password form
2. LDAP authentication
3. OTP verification (optional)
4. User profile completion

This configuration enables seamless single sign-on across all services in the Jahia Experience Suite.

## Jahia Configuration : provisioning/provisioning.yaml

The `provisioning.yaml` file defines the automated setup and configuration of the Jahia Experience Suite environment. It
follows a declarative approach to install components, configure services, and integrate various features.

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
        - To test the CAS authentication, just
          hit [this link](https://keycloak.localhost/realms/realm-idp/protocol/cas/login?service=https%3A%2F%2Fdigitall.jahia.localhost%2Fcms%2Frender%2Flive%2Fen%2Fsites%2Fdigitall%2Fhome.html)

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

This configuration creates a fully functional Jahia environment with integrated authentication, personalization, search,
and demo content.

## Traefik Configuration in the Jahia Experience Suite

Based on the provided information, Traefik serves as the modern reverse proxy and load balancer for the Jahia Experience
Suite environment.

### General Traefik Configuration

Traefik is configured using Docker labels on each service container to:

- Define routing rules
- Enable TLS/SSL if needed
- Configure middleware
- Set load balancing options

### Service Routing

Each service in the stack is accessible through hostname-based routing:

- `jahia.localhost` → Jahia DXP
- `jcustomer.localhost` → jCustomer (Unomi)
- `keycloak.localhost` → Keycloak authentication server
- `phpldapadmin.localhost` → LDAP admin interface
- `phpmyadmin.localhost` → Database admin interface
- `kibana.localhost` → Elasticsearch visualization

### Docker Labels Pattern

The Docker Compose file would typically include labels like:

```yaml
services:
  jahia:
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.jahia.rule=Host(`jahia.localhost`)"
      - "traefik.http.services.jahia.loadbalancer.server.port=8080"
```

Here is more thorough explanation of the labels:
```yaml
    labels:
      - "traefik.enable=true"
      - "traefik.docker.network=stack"
      - "traefik.http.routers.jahiabrowsing.rule=Host(`jahiabrowsing.localhost`) || HostRegexp(`^.+\\.jahiabrowsing\\.localhost$`)"
      - "traefik.http.routers.jahiabrowsing.entrypoints=websecure"
      - "traefik.http.routers.jahiabrowsing.tls=true"
      - "traefik.http.routers.jahiabrowsing.service=jahiabrowsing-http"
      - "traefik.http.services.jahiabrowsing-http.loadbalancer.server.port=8080"
      - "traefik.http.services.jahiabrowsing-http.loadbalancer.sticky.cookie.httponly=true"
      - "traefik.http.services.jahiabrowsing-http.loadbalancer.sticky.cookie.name=jahia_session"
      - "traefik.http.services.jahiabrowsing-http.loadbalancer.sticky.cookie.secure=true"
      - "traefik.http.routers.jahiabrowsing-redirect.rule=Host(`jahiabrowsing.localhost`)|| HostRegexp(`^.+\\.jahiabrowsing\\.localhost$`)"
      - "traefik.http.routers.jahiabrowsing-redirect.entrypoints=web"
      - "traefik.http.routers.jahiabrowsing-redirect.middlewares=redirect-to-https"
      - "traefik.http.middlewares.redirect-to-https.redirectscheme.scheme=https"
```

### Network Configuration

Traefik connects to the `stack` network (subnet `172.16.1.0/24`) to access all services in the environment, serving as
the entry point for external requests.

## TLS/SSL Configuration
Traefik is set up to handle TLS termination, ensuring secure HTTPS connections for all services. Certificates can be
managed using Let's Encrypt or self-signed certificates for local development.

To generate self-signed certificates, you can use the following OpenSSL command, from the root of the project:

```bash
openssl req -x509 -nodes -days 365 -newkey rsa:4096 -keyout volumes/traefik/certs/localhost.key -out volumes/traefik/certs/localhost.crt -subj "/C=CA/ST=ON/L=Toronto/O=Jahia/CN=localhost" -addext "subjectAltName=DNS:localhost,DNS:*.localhost"
```

## Mkcert Configuration
Alternatively, you can use mkcert to generate locally trusted certificates, mkcert installation will simplify the process of creating and trusting self-signed certificates for local development, it is adding CA to your system trust store.
First, run ./setup-certs.sh to install mkcert and generate the certificates.

```bash
./setup-certs.sh
```

Then change the reverse-proxy service in the docker-compose.yml to use the generated mkcert files:
```yaml
    #      - ./volumes/traefik/tls.yml:/etc/traefik/tls.yml:ro # Traefik TLS configuration
    - ./volumes/traefik/tlsmkcert.yml:/etc/traefik/tls.yml:ro # Traefik TLS configuration with mkcert
```

Then recreate the traefik container:
```bash
docker compose up -d reverse-proxy
```

Communication between services within the Docker network can remain unencrypted for simplicity.

if you access the Traefik dashboard at [http://localhost:9080/dashboard/](http://localhost:9080/dashboard/), you should see that all routers have TLS enabled.

Requests to services like Jahia, Keycloak, and jCustomer will be securely handled by Traefik and so access to http URLs will be redirected to https.
[https://jahia.localhost](https://jahia.localhost), [https://keycloak.localhost](https://keycloak.localhost), [https://jcustomer.localhost](https://jcustomer.localhost)

[//]: # (For production environments, it's recommended to use valid SSL certificates from a trusted CA.)

### Load Balancing Features

Since the environment includes multiple services with varying resource requirements, Traefik likely handles:

- Health checking
- Request distribution
- Traffic prioritization based on service types

The Docker labels provide a declarative way to define how Traefik should direct incoming traffic to the appropriate
containers without needing a separate configuration file.

## Jahia Experience Suite Environment User

Two users are created in the Jahia Experience Suite environment:

- **ovansk**: The default administrator user with full access to all features and settings. (Username: `ovansk`,
  Password: `Ovans`)
- **monzos**: An editor in chief user for digitall and luxe website. (Username: `monzos`, Password: `Monzo`)
- **lauxc**: A regular user for digitall and luxe website. (Username: `lauxc`, Password: `Laux`)

## CAS Link

To test the CAS authentication, just
hit [this link](https://keycloak.localhost/realms/realm-idp/protocol/cas/login?service=https%3A%2F%2Fdigitall.jahia.localhost%2Fcms%2Frender%2Flive%2Fen%2Fsites%2Fdigitall%2Fhome.html).


## Clustering
The Jahia Experience Suite environment is designed to support clustering for high availability and load balancing.
This uses Docker Compose scale feature to create multiple instances of Jahia Browsing instances.

### Scaling Jahia Browsing
To scale the Jahia Browsing service, you can use the following command:

```bash
JAHIA_CLUSTER_ENABLED=true BROWSING_NODES=2 COMPOSE_PROFILES=cluster docker-compose up -d
```

You can also update your `.env` file
```env
JAHIA_CLUSTER_ENABLED=true
BROWSING_NODES=2
COMPOSE_PROFILES=cluster,admin
```

Once the cluster is started you can scale jahia-browsing service using the following command:
```bash
docker-compose up -d --scale jahia-browsing=2
docker-compose up -d --scale jahia-browsing=1
```
