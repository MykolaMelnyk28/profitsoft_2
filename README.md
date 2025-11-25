# ProfITsoft - Task 2

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
| `GET`    | `/api/authros/{id}`  | Get a `Genre` by its `id`                                              |
| `POST`   | `/api/authors`       | Create a new `Author`                                                  |
| `PUT`    | `/api/authors/{id}`  | Update an `Author` by its `id`                                         |
| `DELETE` | `/api/authors/{id}`  | Delete an `Author` by its `id`                                         |
| `POST`   | `/api/genres/_list`  | Search for `Genres` using filters                                      |
| `GET`    | `/api/genres/{id}`   | Get a `Genre` by its `id`                                              | 
| `POST`   | `/api/genres`        | Create a new `Genre`                                                   |
| `PUT`    | `/api/genres/{id}`   | Update a `Genre` by its `id`                                           |
| `DELETE` | `/api/genres/{id}`   | Delete a `Genre` by its `id`                                           |

