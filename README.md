# Demo task

# [Local development setup](https://start.spring.io/)

Задание

Имеется множество объектов (зданий), состояние которых контролируется с помощью датчиков. С одним объектом может быть связано несколько датчиков. Все датчики измеряют один параметр (температуру) и отправляют измерения на сервер.
Надо реализовать сервис, который будет хранить измерения датчиков и помогать пользователям работать с этими данными. Доступ к сервису должен выполняться через REST API со следующими функциями:
```url
•	/api/save: сохранить измерения датчиков, передаваемые как список json структур 
                   (объект, датчик, время в секундах, значение).
```
```json
[
    {
    "objectId": 1,
    "sensorId": 1,
    "time": 1565796600,
    "value": 9.4
    },
    {
    "objectId": 1,
    "sensorId": 2,
    "time": 1565796720,
    "value": 14.2
    }
]
```

**API**
```url
•	/api/history: выдать все измерения заданного датчика за заданный интервал времени 
```
Пример запроса: /api/history?sensorId=2&objectId=1&from=1565654400&to=1565827200
```url
•	/api/latest: выдать текущие (последние на данных момент) значения всех датчиков для заданного объекта. 
```
Пример запроса: /api/latest?objectId=1
```url
•	/api/avg: выдать среднее из текущих значений датчиков для каждого объекта.
```
Реализация сервиса, включая запросы к базе данных, должна обеспечивать эффективную работу с большим числом (суммарно более 1М измерений по всем датчикам) хранимых данных.
Для тестирования приложения к заданию предоставляется генератор тестовых данных generate_sensor_data.py.
generate_sensor_data.py - скрипт на python, генерирующий данные для тестового задания по Java. По-умолчанию (без аргументов) генерирует 2.592 М точек = по 3 объектам и 10 сенсорам с каждого объекта и 86400 наблюдений с каждого сенсора. Результат генерации - это валидный json документ - список точек с данными
Подготовительные действия:
chmod 755 ./generate_sensor_data.py
Просмотр помощи:
python ./generate_sensor_data.py --help
Пример запуска генератора:
python ./generate_sensor_data.py

Пожелания и требования
Требования к используемым инструментам: Java 8 или выше, Spring Framework, Gradle/Maven.
Помимо реализации сервиса решение должно включать набор юнит тестов для проверки функционала сервиса и минимальную документацию (ReadMe с примерами сборки, запуска приложения и юнит тестов).

# Run locally:
    gradle bootRun
App has init data.json as resource. DB is initialized on startup
# Build:
    gradle build
# Run tests:
    gradle test
