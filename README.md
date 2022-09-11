# Artinaut

Artinaut is a tiny and fast Maven repository manager based on the popular Micronaut framework. It allows you to define local, remote and virtual repositories and can act as an internal Maven repository proxy within your organisation.

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

### Release 0.2 🐣 - Do first steps

- ⏳ Upload artifacts to local repositories


