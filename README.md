# Users API

REST API for managing people. List, search, filter, create, edit, delete. Built with Spring Boot, JPA, and MySQL.

## Client

The front end for this API lives here:

https://github.com/ioannvlac-hub/users-api

## Data

A user has a name, surname, gender, and birthdate. Addresses sit in their own table, one row per address, tagged home or work.

That way a user can have both, one, or none, and adding a third kind later needs no change to the user table.

## The list query

The list endpoint does not load users. It uses a projection, so the query selects only id, name, and surname straight into an interface.

Gender and search are optional in the same query. Each is skipped when null, so one method covers all four combinations instead of four methods.

## Address updates

`updateAddress` reads the incoming value three ways.

Null means leave it alone. Empty string means clear it. Text means set it, whether or not one was there before.

That is what lets the edit form send a partial update without wiping the fields it did not touch.

## Errors

`ApiException` carries a status and a message. Services throw it, `GlobalExceptionHandler` turns it into a response.

Anything unexpected is caught and rethrown as a 500 with a plain message, so a stack trace never reaches the client.

## Layout

```
controller/    endpoints
service/       the rules
repository/    JPA, plus the search query
entity/        User, Address, enums
dto/           request and response shapes
exception/     ApiException and the handler
```

DTOs are separate from entities on purpose. The database shape and the API shape can change without dragging each other along.

## Run it

Start MySQL:

```
docker compose up -d
```

Then the app:

```
./mvnw spring-boot:run
```

Runs on port 8080. Tables are created on first start.

The database settings in `application.properties` match the docker compose file, so nothing needs editing to run it locally.

## MIT Licenseed
