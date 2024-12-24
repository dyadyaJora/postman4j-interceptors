# Postman4j Interceptors

[![Maven Central Version](https://img.shields.io/maven-central/v/dev.jora.postman4j/postman4j-models)](https://central.sonatype.com/artifact/dev.jora.postman4j/postman4j-models)


## Instllation

In gradle
```gradle
implementation group: 'dev.jora.postman4j', name: 'postman4j-models', version: '0.0.1'
```

In maven
```xml
<dependency>
    <groupId>dev.jora.postman4j</groupId>
    <artifactId>postman4j-models</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Usages

Example of instrumenting custom function in your java code

```java
@UsePostmanCollection("My Test Collection")
@UsePostmanFolderPath("Folder #1")
@UsePostmanRequest("My Request")
@UsePostmanResponse("My Response")
public static void executeRequest(CloseableHttpClient httpClient, HttpUriRequestBase request) throws IOException {
    try (CloseableHttpResponse response = httpClient.execute(request)) {
        System.out.println(response.getCode());
    }
}
```
