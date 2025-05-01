#!/bin/bash

# Create a temporary file
temp_file=$(mktemp)

# Process the LDIF file
while IFS= read -r line; do
  # Check if the line starts with "cn:"
  if [[ "$line" =~ ^cn:[[:space:]]+([A-Za-z]+)$ ]]; then
    cn_value="${BASH_REMATCH[1]}"
    cn_lowercase=$(echo "$cn_value" | tr '[:upper:]' '[:lower:]')

    # Only add if the lowercase version is different from original
    if [[ "$cn_value" != "$cn_lowercase" ]]; then
      echo "cn: $cn_lowercase" >>"$temp_file"
    fi
  else
    # Write non-CN lines to the output file
    echo "$line" >>"$temp_file"
  fi
done <./volumes/ldap/ldifs/01-performance.ldif

# Replace the original file with the modified one
mv "$temp_file" ./volumes/ldap/ldifs/01-performance.ldif
