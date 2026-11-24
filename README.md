# WebFlux Filters / WebFlux 필터

[ENG](#eng) | [KR](#kr)

## ENG

### Project structure

```text
pom.xml
src/main/java/io/github/parkwoong/gateway/Application.java
src/main/java/io/github/parkwoong/gateway/filter/HistoryLogger.java
src/main/java/io/github/parkwoong/gateway/filter/RequestDecorator.java
src/main/java/io/github/parkwoong/gateway/handler/SampleHandler.java
src/main/java/io/github/parkwoong/gateway/history/History.java
src/main/java/io/github/parkwoong/gateway/history/HistoryIdClass.java
src/main/java/io/github/parkwoong/gateway/history/HistoryRepository.java
src/main/java/io/github/parkwoong/gateway/properties/FilterProperties.java
src/main/java/io/github/parkwoong/gateway/util/GlobIdGenerator.java
src/main/resources/application.properties
src/test/java/io/github/parkwoong/gateway/FilterTest.java
```

### Code explanation

RequestDecorator adds a request ID to the exchange. HistoryLogger reads and replays the request
body, wraps the response, and stores request metadata in H2. The sample handler returns the POST
body. Stored headers and bodies are redacted.

### Run and test

Use JDK 8 and Maven 3.6.3+.

```sh
java -version
mvn clean verify
mvn spring-boot:run
```

POST text to /sample. FilterTest checks request ID creation. The application listens on
localhost:8080.

#### Postman API

##### Echo through the filters

**Request**

- Method: POST
- URL: http://localhost:8080/sample
- Content-Type: text/plain

~~~text
hello
~~~

**Response**

- Status: 200 OK

~~~text
hello
~~~

## KR

### 프로젝트 구조

```text
pom.xml
src/main/java/io/github/parkwoong/gateway/Application.java
src/main/java/io/github/parkwoong/gateway/filter/HistoryLogger.java
src/main/java/io/github/parkwoong/gateway/filter/RequestDecorator.java
src/main/java/io/github/parkwoong/gateway/handler/SampleHandler.java
src/main/java/io/github/parkwoong/gateway/history/History.java
src/main/java/io/github/parkwoong/gateway/history/HistoryIdClass.java
src/main/java/io/github/parkwoong/gateway/history/HistoryRepository.java
src/main/java/io/github/parkwoong/gateway/properties/FilterProperties.java
src/main/java/io/github/parkwoong/gateway/util/GlobIdGenerator.java
src/main/resources/application.properties
src/test/java/io/github/parkwoong/gateway/FilterTest.java
```

### 코드 설명

RequestDecorator가 exchange에 요청 ID를 넣는다. HistoryLogger가 요청 본문을 읽고 다시
전달하며 응답을 감싼 뒤 요청 메타데이터를 H2에 저장한다. 샘플 핸들러는 POST 본문을 반환한다. 저장되는 헤더와
본문은 가린다.

### 실행과 테스트

JDK 8, Maven 3.6.3 이상 사용. 저장소 루트에서 실행.

```sh
java -version
mvn clean verify
mvn spring-boot:run
```

/sample에 텍스트를 POST . FilterTest는 요청 ID 생성을 확인한다. 앱은 localhost:8080에서
실행된다.

#### Postman API

##### 필터를 거쳐 본문 반환

**요청**

- 메서드: POST
- URL: http://localhost:8080/sample
- Content-Type: text/plain

~~~text
hello
~~~

**응답**

- 상태: 200 OK

~~~text
hello
~~~
