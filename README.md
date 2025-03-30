# Knote-Java

Knote es una aplicación de notas simples basada en Spring Boot y MongoDB. Esta guía explica cómo ejecutar la aplicación utilizando Docker.

## Requisitos previos

- [Docker](https://www.docker.com/) (versión 20.10 o superior).
- Acceso a la imagen pública en Docker Hub: [`imyisus/knote-java`](https://hub.docker.com/r/imyisus/knote-java).

---

## Ejecutar la aplicación

### 1. Crear una red Docker
```bash
docker network create knote
```

### 2. Iniciar MongoDB 
```bash
docker run \
  --name=mongo \
  --rm \
  --network=knote \
  mongo
```

### 3. Iniciar Knote
```bash
docker run \
  --name=knote-java \
  --rm \
  --network=knote \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/dev \
  imyisus/knote-java
```

### 4. Acceder a la aplicación

Visita http://localhost:8080/ en tu navegador.

#### Variables de entorno

| VARIABLE | DESCRIPCIÓN               | VALOR PREDETERMINADO |
| --- |---------------------------| --- |
| SPRING_DATA_MONGODB_URI | URI de conexión a MongoDB | mongodb://mongo:27017/dev |
| PORT | Puerto de la aplicación | 8080 |

### Docker Hub

La imagen está disponible en:

[https://hub.docker.com/r/imyisus/knote-java](https://hub.docker.com/r/imyisus/knote-java)