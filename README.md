# Artinaut

Artinaut is a tiny and fast Maven repository manager based on the popular Micronaut framework. It allows you to define local, remote and virtual repositories and can act as an internal Maven repository proxy within your organisation.

![example workflow](https://github.com/saw303/artinaut/actions/workflows/release.yml/badge.svg)

## What's that name?

The name _Artinaut_ is a combination of the words _Micronaut_ and _Artifacts_.

## Motivation

Due to the fact that common Java based repository managers such as Artifactory are heavy weight applications, require quiet an amount of memory at runtime and have a slow start-up time, they aren't first class citizen in the cloud.

Artinaut aims to solve these major problems and provide a small Java application that is published as a Docker image. The Docker image easily can be deployed using Docker Compose, Kubernetes or other Docker based environments.

## Roadmap

### Release 0.1 🥚 - Build the basics

- ✅ Support for proxying remote repositories with caching options
- ✅ Support for local repositories
- ✅ Support for virtual repositories (combine a collection of local or remote repositories and expose it as one repository)
- ✅ Support for MariaDB 10.3+
- ✅ Support for Group management
- ⏸ Support for User management

### Release 0.2 🐣 - Do first steps

- ⏳ Upload artifacts to local repositories

## Example 🐳 Composition 

The following `docker-compose.yml` will help you to setup Artinaut as a Docker Composition.

```yaml
version: "3.9"
services:
  database:
    image: mariadb:10.8.3-jammy
    environment:
      MYSQL_ROOT_PASSWORD: root_secret
      MYSQL_DATABASE: artinaut
      MYSQL_USER: artinaut
      MYSQL_PASSWORD: secret
      TZ: "Europe/Zurich"
      MARIADB_AUTO_UPGRADE: "true"
    volumes:
      - ./dockervolumes/artinaut-db:/var/lib/mysql
    ports:
      - "127.0.0.1:3306:3306"
    deploy:
      resources:
        limits:
          cpus: "1.0"
          memory: 200M

  artinaut:
    image: ghcr.io/saw303/artinaut/artinaut:0.1.2
    environment:
      DB_HOST: database
      DB_NAME: artinaut
      DB_USER: artinaut
      DB_PWD: secret
      ARTINAUT_FILESTORE: /home/app/filestore
    ports:
      - "127.0.0.1:8080:8080"
    restart: on-failure
    deploy:
      resources:
        limits:
          cpus: "3.0"
          memory: 200M
```

Run `docker compose up -d` to start Artinaut. Afterwards the application is running on `localhost:8080` and has the following preconfigured remote repositories available:

- Maven Central ➡️ https://repo.maven.apache.org/maven2
- Gradle Plugins ➡️ https://plugins.gradle.org/m2


