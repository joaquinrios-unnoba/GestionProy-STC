# GestionProy-STC #
Proyecto UNNOBA

# Configuración de credenciales OAuth #

1. Crear un proyecto en https://console.cloud.google.com/
2. Ir a "Credenciales" y crear un "ID de cliente de OAuth"
3. Usar esta URL de redirección: http://localhost:8080/oauth2/callback
4. Crear un archivo llamado .env en la carpeta backend con:

GOOGLE_CLIENT_ID=client-id
GOOGLE_CLIENT_SECRET=client-secret

Nombre ID del proyecto en GoogleCloud: GestionProy-Spring
Nombre del proyecto, pero en Informacion de la app: GestionProy-STC-Spring
ID del cliente OAuth: 
Secret del cliente:
