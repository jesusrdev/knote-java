# Knote-Java

Knote es una aplicación de notas simples basada en Spring Boot y MongoDB. Esta guía explica cómo ejecutar la aplicación utilizando Docker.

## Requisitos previos

- [Docker](https://www.docker.com/) (versión 20.10 o superior).
- Acceso a la imagen pública en Docker Hub: [`imyisus/knote-java`](https://hub.docker.com/r/imyisus/knote-java).
- Kubernetes (Minikube)

---

## Ejecutar la aplicación con Minikube

### 1. Enviar las definiciones de la aplicación a Kubernetes:

```bash
kubectl apply -f kube
```

### 2. Acceder a la aplicación:

```bash
minikube service knote --url
```
