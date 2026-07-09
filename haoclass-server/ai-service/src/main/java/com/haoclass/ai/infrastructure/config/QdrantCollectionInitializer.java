package com.haoclass.ai.infrastructure.config;

import com.haoclass.ai.infrastructure.config.properties.AiQdrantProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Qdrant集合初始化器。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QdrantCollectionInitializer implements ApplicationRunner {

    private final AiQdrantProperties qdrantProperties;

    @Override
    public void run(ApplicationArguments args) {
        if (!Boolean.TRUE.equals(qdrantProperties.getAutoCreateCollection())) {
            return;
        }

        if (collectionExists()) {
            log.info("Qdrant集合已存在, collectionName: {}", qdrantProperties.getCollectionName());
            return;
        }

        createCollection();
    }

    private boolean collectionExists() {
        try {
            HttpResponse<String> response = send(HttpRequest.newBuilder(collectionUri()).GET().build());
            if (response.statusCode() == 200) {
                return true;
            }
            if (response.statusCode() == 404) {
                return false;
            }
            throw new IllegalStateException("检查Qdrant集合失败, status: " + response.statusCode() + ", body: " + response.body());
        } catch (Exception e) {
            throw new IllegalStateException("检查Qdrant集合失败，请确认Qdrant已启动", e);
        }
    }

    private void createCollection() {
        try {
            String body = """
                    {
                      "vectors": {
                        "size": %d,
                        "distance": "Cosine"
                      }
                    }
                    """.formatted(qdrantProperties.getVectorSize());

            HttpRequest request = HttpRequest.newBuilder(collectionUri())
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = send(request);

            if (response.statusCode() != 200) {
                throw new IllegalStateException("创建Qdrant集合失败, status: " + response.statusCode() + ", body: " + response.body());
            }

            log.info("Qdrant集合创建成功, collectionName: {}, vectorSize: {}",
                    qdrantProperties.getCollectionName(),
                    qdrantProperties.getVectorSize());
        } catch (Exception e) {
            throw new IllegalStateException("创建Qdrant集合失败", e);
        }
    }

    private HttpResponse<String> send(HttpRequest request) throws Exception {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI collectionUri() {
        return URI.create(String.format(
                "http://%s:%d/collections/%s",
                qdrantProperties.getHost(),
                qdrantProperties.getPort(),
                URLEncoder.encode(
                        qdrantProperties.getCollectionName(),
                        StandardCharsets.UTF_8
                )
        ));
    }
}
