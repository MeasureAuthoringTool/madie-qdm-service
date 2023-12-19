# madie-qdm-service

Quantity Data Model service for MADiE application.

To build 
```
mvn clean verify 
```

To run:
```
mvn install spring-boot:run 
```

or
```
docker compose up 
```


To test actuator locally
```
http://localhost:8086/api/actuator/health 
```

To test app locally
```
http://localhost:8086/api
```
should give response unauthorized (HTTP ERROR 401)
