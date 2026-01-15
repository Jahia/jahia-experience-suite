#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Setting up local SSL certificates with mkcert...${NC}\n"

# Check if mkcert is installed
if ! command -v mkcert &> /dev/null; then
    echo -e "${RED}Error: mkcert is not installed${NC}"
    echo -e "${YELLOW}Install it with: brew install mkcert${NC}"
    exit 1
fi

# Install local CA if not already installed
echo -e "${YELLOW}Installing local CA...${NC}"
mkcert -install

# Create certs directory if it doesn't exist
CERTS_DIR="./volumes/traefik/certs"
mkdir -p "$CERTS_DIR"

# Extract all .localhost domains from docker-compose.yml
echo -e "${YELLOW}Extracting domains from docker-compose.yml...${NC}"
DOMAINS=$(grep -oE '`[a-zA-Z0-9_-]+\.localhost`' docker-compose.yml | \
          sed 's/`//g' | \
          sort -u | \
          tr '\n' ' ')

# Add base localhost domains and wildcard subdomains
ALL_DOMAINS="localhost $DOMAINS *.jahia.localhost *.jahiabrowsing.localhost"

echo -e "${YELLOW}Generating certificate for the following domains:${NC}"
echo -e "${GREEN}$ALL_DOMAINS${NC}\n"

# Generate certificates
mkcert -cert-file "$CERTS_DIR/localhost.pem" \
       -key-file "$CERTS_DIR/localhost-key.pem" \
       $ALL_DOMAINS

# Check if certificates were generated successfully
if [ -f "$CERTS_DIR/localhost.pem" ] && [ -f "$CERTS_DIR/localhost-key.pem" ]; then
    echo -e "${GREEN}✓ Certificates generated successfully!${NC}"
    echo -e "${GREEN}✓ Location: $CERTS_DIR${NC}"

    # Set appropriate permissions
    chmod 644 "$CERTS_DIR/localhost.pem"
    chmod 600 "$CERTS_DIR/localhost-key.pem"

    echo -e "${GREEN}✓ Permissions set${NC}\n"

    # List generated files
    echo -e "${YELLOW}Generated files:${NC}"
    ls -lh "$CERTS_DIR"

    echo -e "\n${GREEN}✓ Setup complete!${NC}"
    echo -e "${YELLOW}Next steps:${NC}"
    echo -e "  1. Update volumes/traefik/tlsmkcert.yml to reference localhost.pem"
    echo -e "  2. Restart Traefik: docker compose up -d reverse-proxy"
else
    echo -e "${RED}✗ Failed to generate certificates${NC}"
    exit 1
fi
