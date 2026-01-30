#!/bin/bash

# Script para generar claves JWT RSA

echo "Generando claves JWT..."

# Crear directorio si no existe
mkdir -p src/main/resources/META-INF/resources

# Generar clave privada RSA de 2048 bits
openssl genrsa -out src/main/resources/META-INF/resources/privateKey.pem 2048

# Generar clave pública desde la privada
openssl rsa -in src/main/resources/META-INF/resources/privateKey.pem -pubout -out src/main/resources/META-INF/resources/publicKey.pem

echo "Claves generadas en src/main/resources/META-INF/resources/"
