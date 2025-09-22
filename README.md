# Linked Data Interactions Orchestrator

This is the spiritual successor project of the [VSDS Linked Data Interactions project](https://github.com/Informatievlaanderen/VSDS-Linked-Data-Interactions)

The Linked Data Interactions Orchestrator (LDIO) serves as a workbench for any Linked Data tinkerer.
Whether you're reading, transforming, exporting, or storing Linked Data, LDIO provides a unified platform to manage it all.

## Getting Started

### Using the provided setup

To get started, simply launch the provided [docker-compose file](./docker-compose.yml).
This will provide you with both a LDIO instance and a Redis database for storing (meta)data.

```shell
docker compose up
```

Next, browse to your instance `http://localhost:8080/` <br>
From there, follow the guide on the home page.

### Using docker images

The image is available at `ghcr.io/yalz/ldio`. For more details on LDIO versioning, visit [the versions page](https://github.com/Yalz/Linked-Data-Interactions/pkgs/container/ldio/versions).

Secondly, a Redis database connection is needed to let LDIO register its pipelines.
Provide the LDIO image with a valid connection string with the environment variable `REDIS_URI`.

## Feedback

We heavily encourage community members to try out and provide feedback.
Do you wish to report a bug? or suggest a new feature? Be sure to create an issue/feature request.