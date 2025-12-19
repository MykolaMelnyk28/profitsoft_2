# ProfITsoft - Task 2

## Technologies
- `Spring Boot 4.0.0`
- `Spring Boot Starters JPA, Validation, Cache, Actuator`
- `PostgreSQL Database 17`
- `Liquibase`
- `Lombok`
- `MapStruct`
- `Apace POI Library`
- `Ehcache`
- `Swagger`
- `Junit 5/Mockito`
- `Zonky embedded postgresql database (in Docker container)`

## Entities

![img.png](diagram.png)

`Book` many to one `Author`

`Book` many to many `Genre`

## Endpoints

| Method   | Endpoint             | Description                                                            |
|----------|----------------------|------------------------------------------------------------------------|
| `POST`   | `/api/books`         | Create a new `Book`                                                    |
| `GET`    | `/api/books/{id}`    | Get a `Book` by its `id`                                               |
| `PUT`    | `/api/books/{id}`    | Update a `Book` by its `id`                                            |
| `DELETE` | `/api/books/{id}`    | Delete a `Book` by its `id`                                            |
| `POST`   | `/api/books/_list`   | Search for `Books` using filters (pagination + sorting supported)      |
| `POST`   | `/api/books/_report` | Search for `Books` using filters and download a generated Excel report |
| `POST`   | `/api/books/upload`  | Upload a JSON file and create all `Books` from its contents            |
| `POST`   | `/api/authors/_list` | Search for `Authors` using filters                                     |
| `GET`    | `/api/authors/{id}`  | Get a `Genre` by its `id`                                              |
| `POST`   | `/api/authors`       | Create a new `Author`                                                  |
| `PUT`    | `/api/authors/{id}`  | Update an `Author` by its `id`                                         |
| `DELETE` | `/api/authors/{id}`  | Delete an `Author` by its `id`                                         |
| `POST`   | `/api/genres/_list`  | Search for `Genres` using filters                                      |
| `GET`    | `/api/genres/{id}`   | Get a `Genre` by its `id`                                              | 
| `POST`   | `/api/genres`        | Create a new `Genre`                                                   |
| `PUT`    | `/api/genres/{id}`   | Update a `Genre` by its `id`                                           |
| `DELETE` | `/api/genres/{id}`   | Delete a `Genre` by its `id`                                           |
| `GET`    | `/actuator/health`   | Returns the server status                                              |

## Running

### 0. Run tests

To run only unit tests:
```bash
mvn test
```

To run all tests, includes integration tests (requires Docker)
```bash
mvn verify
```

### 1. Create a `postgres.env` file in the project root:

This file is used by the PostgreSQL Docker container (if using Docker)

```dotenv
POSTGRES_DB=book_db
POSTGRES_USER=<Database username>
POSTGRES_PASSWORD=<Databadse password>
```

### 2. Create a `.env` file in the project root:

This file is used by the Spring Boot application.

```dotenv
# Server
SERVER_PORT=8080
SERVER_SSL_KEYSTORE_PATH=<Key store path>
SERVER_SSL_KEY_PASSWORD=<Key password>
SERVER_SSL_KEY_STORE_PASSWORD=<Key store password>
SERVER_SSL_KEY_STORE_TYPE=<Key store type>
SERVER_SSL_KEY_ALIAS=<Key alias>

# Spring
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_USERNAME=<Database username>
SPRING_DATASOURCE_PASSWORD=<Databadse password>
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/book_db

# Cors
CORS_ALLOWED_ORIGINS=*
```

### 3. Run with Docker

Before starting, make sure you have created the following files:

- `<project root>/.env`
- `<project root>/postgres.env`

Run the command:
```bash
docker-compose up -d book-api-app
```

---

## API urls
- API base url: `https://localhost:8080`
- Health endpoint: `https://localhost:8080/actuator/health`
- Swagger page: `https://localhost:8080/swagger-ui/index.html`

---

## Upload books

For upload use `data/upload.json` file. 

> `data/upload.json` file assumes that you have already created 4 authors with ids 1,2,3,4 and created 5 genres with ids 1,2,3,4,5

## API Examples

### Health endpoint
```bash
curl -s -X GET https://localhost:8080/actuator/health | jq
```

### Create book
```bash
curl -s -X POST https://localhost:8080/api/books \
    -H 'Content-Type: application/json' \
    -d '{
	  "title": "book1",
	  "authorId": 2,
	  "yearPublished": 2025,
	  "pages": 501,
	  "genreIds": [1, 2]
    }' | jq
```

### Get a book by id
```bash
curl -s -X GET https://localhost:8080/api/books/<ID> | jq
```

### Search book by filter (each filter field is optional, but exactly filter object body is required)
```bash
curl -s -X POST https://localhost:8080/api/books/_list \
    -H 'Content-Type: application/json' \
    -d '{
      "query": "book1",
      "minYearPublished": 2000,
      "maxYearPublished": 2025,
      "authorIds": [1, 2, 3],
      "minPages": 500,
      "maxPages": 600,
      "genreIds": [1, 2],
      "page": 0,
      "size": 10,
      "sort": "title,desc",
      "startCreatedAt": "2025-12-19T18:35:50.941Z",
      "endCreatedAt": "2025-12-19T18:35:50.941Z",
      "startUpdatedAt": "2025-12-19T18:35:50.941Z",
      "endUpdatedAt": "2025-12-19T18:35:50.941Z"
    }' | jq
```

### Update a book by id
```bash
curl -s -X PUT https://localhost:8080/api/books/<ID> \
  -H "Content-Type: application/json" \
  -d '{
	  "title": "book1Updated",
	  "authorId": 2,
	  "yearPublished": 2025,
	  "pages": 501,
	  "genreIds": [3, 4]
    }' | jq
```

### Delete book by id
```bash
curl -s -X DELETE https://localhost:8080/api/books/<ID> | jq
```


### Generate books Excel Report 
```bash
curl -s -X POST https://localhost:8080/api/books/_report \
  -H "Content-Type: application/json" \
  -d '{
      "query": "book1",
      "minYearPublished": 2000,
      "maxYearPublished": 2025,
      "authorIds": [1, 2, 3],
      "minPages": 500,
      "maxPages": 600,
      "genreIds": [1, 2],
      "page": 0,
      "size": 10,
      "sort": "title,desc",
      "startCreatedAt": "2025-12-19T18:35:50.941Z",
      "endCreatedAt": "2025-12-19T18:35:50.941Z",
      "startUpdatedAt": "2025-12-19T18:35:50.941Z",
      "endUpdatedAt": "2025-12-19T18:35:50.941Z"
    }' -o books_report.xlsx
```

### Upload books from JSON File
```bash
curl -s -X POST https://localhost:8080/api/books/upload \
    -H 'Content-Type: multipart/form-data' \
    -F "file=@data/upload.json;type=application/json" | jq
```

### Create an author
```bash
curl -s -X POST https://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Robert",
    "lastName": "Martin"
  }' | jq
```

### Get author by id
```bash
curl -s -X GET https://localhost:8080/api/authors/<ID> | jq
```

### Search authors by filter (each filter field is optional, but exactly filter object body is required)
```bash
curl -s -X POST https://localhost:8080/api/authors/_list \
    -H 'Content-Type: application/json' \
    -d '{
      "query": "firstName, lastName",
      "firstName": "firstName",
      "lastName": "lastName",
      "page": 0,
      "size": 10,
      "sort": "lastName,desc",
      "startCreatedAt": "2025-12-19T18:35:50.941Z",
      "endCreatedAt": "2025-12-19T18:35:50.941Z",
      "startUpdatedAt": "2025-12-19T18:35:50.941Z",
      "endUpdatedAt": "2025-12-19T18:35:50.941Z"
    }' | jq
```

### Update an author by id
```bash
curl -s -X PUT https://localhost:8080/api/authors/<ID> \
    -H 'Content-Type: application/json' \
    -d '{
      "firstName": "Robert",
      "lastName": "Lison"
    }' | jq
```

### Delete an author by id
```bash
curl -s -X DELETE https://localhost:8080/api/authors/<ID> | jq
```

### Create a genre
```bash
curl -s -X POST https://localhost:8080/api/genres \
  -H "Content-Type: application/json" \
  -d '{ "name": "Fantasy" }' | jq
```

### Get genre by id
```bash
curl -s -X GET https://localhost:8080/api/genres/<ID> | jq
```

### Search genres by filter (each filter field is optional, but exactly filter object body is required)
```bash
curl -s -X POST https://localhost:8080/api/genres/_list \
    -H 'Content-Type: application/json' \
    -d '{
      "query": "Fantasy",
      "page": 0,
      "size": 10,
      "sort": "name,desc",
      "startCreatedAt": "2025-12-19T18:35:50.941Z",
      "endCreatedAt": "2025-12-19T18:35:50.941Z",
      "startUpdatedAt": "2025-12-19T18:35:50.941Z",
      "endUpdatedAt": "2025-12-19T18:35:50.941Z"
    }' | jq
```

### Update a genres by id
```bash
curl -s -X PUT https://localhost:8080/api/genres/<ID> \
    -H 'Content-Type: application/json' \
    -d '{ "name": "updatedName" }' | jq
```

### Delete a genres by id
```bash
curl -s -X DELETE https://localhost:8080/api/genres/<ID> | jq
```

