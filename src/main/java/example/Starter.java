package example;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class Starter {

    public static void main(String[] args) {
        new Starter().pingDockerSystemService();
    }

    public void pingDockerSystemService() {

        final DockerClientConfig config =

                // FIXME: Works on Mac, fails on Windows:

                DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        // FIXME: Works on Windows (when "expose api without tls" in docker desktop settings, is activated :

//                DefaultDockerClientConfig.createDefaultConfigBuilder()
//                        .withDockerHost("tcp://localhost:2375")
//                        .withDockerTlsVerify(false)
//                        .build();

        final DockerHttpClient http = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        final var request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path("/_ping")
                .build();

        try (var response = http.execute(request)) {
            final var status = response.getStatusCode();
            log.warn("Response code was: {}", status);
        } catch (Exception e) {
            log.warn("http: {}", e.getMessage());
        }
    }
}
