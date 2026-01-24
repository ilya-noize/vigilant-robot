# vigilant-robot
Бдительный робот!

> Консольное банковское приложение на Java с использованием Spring Framework,
предоставляющее базовые функции для управления пользователями и их банковскими
счетами.

## Pull Requests

| PR | About                                              |
|----|----------------------------------------------------|
| #1 | Base stack                                         |
| #2  | Models: User, Account                              |
| #3  | configurations, handlers, properties, interfaces   |
| #4  | I/O handler, Long -> int, Interface S.         |
| #5  | clean *boilerplate*, finish test, testable-methods |

## Features

- custom menu
- unique login
- commission = 3%
- start amount = 5000 points
- пользователь Админ (ID:1) с админ-счётом(ID:1):
    - все комиссии за переводы между счетами - на счёт админа,
    - перевод со счёта админа - **запрещен**
    - удаление счёта админа - **запрещено**
    - пополнение или переводы (донат + комиссия) на счёт админа **разрешены**.

## Start database

### Docker

В консоли выполнить команду для скачивания PostrgeSQL образа

> docker pull postgres

Запустить контейнер при помощи команды

> docker run --name my-postgres -p 5432:5432 -e POSTGRES_PASSWORD=root -d postgres


В консоли выполнить команду для получения прямого доступа к базе данных в контейнере

> docker exec -it my-postgres psql -U postgres 