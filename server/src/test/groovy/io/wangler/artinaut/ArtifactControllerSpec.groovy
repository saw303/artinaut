package io.wangler.artinaut

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class ArtifactControllerSpec extends Specification implements ArtinautTestPropertyProvider {

    @Inject
    ArtifactClient artifactClient

    @Inject
    ArtifactClientAnonymous artifactClientAnonymous

    void "Request authenticated"() {

        when:
        HttpResponse<?> res = artifactClient.readArtifact('mavenCentral', 'ch.onstructive', 'onstructive-micronaut-commons', '1.0.0', 'onstructive-micronaut-commons-1.0.0.pom')

        then:
        noExceptionThrown()

        and:
        res.status == HttpStatus.OK
    }

    void "Request anonymous"() {

        when:
        HttpResponse<?> res = artifactClientAnonymous.readArtifact('mavenCentral', 'ch.onstructive', 'onstructive-micronaut-commons', '1.0.0', 'onstructive-micronaut-commons-1.0.0.pom')

        then:
        HttpClientResponseException ex = thrown(HttpClientResponseException)

        and:
        ex.status == HttpStatus.UNAUTHORIZED
    }

    @Client('/')
    @Header(name = HttpHeaders.AUTHORIZATION, value = '${artinaut.test.admin.basic-auth-header.value}')
    static interface ArtifactClient {

        @Get('/repos/{repoKey}/{groupId}/{artifactId}/{version}/{filename}')
        HttpResponse<?> readArtifact(String repoKey, String groupId, String artifactId, String version, String filename)
    }

    @Client('/')
    static interface ArtifactClientAnonymous {

        @Get('/repos/{repoKey}/{groupId}/{artifactId}/{version}/{filename}')
        HttpResponse<?> readArtifact(String repoKey, String groupId, String artifactId, String version, String filename)
    }
}
