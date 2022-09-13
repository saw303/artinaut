package io.wangler.artinaut

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.wangler.artinaut.artifactresolvers.ArtifactResolver
import io.wangler.artinaut.artifactresolvers.RemoteArtifactResolver
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Specification

@MicronautTest
class ArtifactControllerSpec extends Specification implements ArtinautTestPropertyProvider {

    @Inject
    ArtifactClient artifactClient

    @Inject
    ArtifactClientAnonymous artifactClientAnonymous

    @Inject
    RemoteArtifactResolver remoteArtifactResolver

    @Shared
    ArtifactDto artifact = new ArtifactDto(
            MediaType.TEXT_XML_TYPE,
            new ByteArrayInputStream('xml'.bytes),
            0L,
            3L
    )

    @MockBean(RemoteArtifactResolver)
    ArtifactResolver remoteArtifactResolver() {
        return new ArtifactResolver() {

            @Override
            boolean supports(Repository repository) {
                return true
            }

            @Override
            Optional<ArtifactDto> resolveArtifact(ArtifactContextDto context) {
                return Optional.of(artifact)
            }
        }
    }

    void "Request artifact authenticated"() {

        when:
        HttpResponse<?> res = artifactClient.readArtifact('mavenCentral', 'ch.onstructive', 'onstructive-micronaut-commons', '1.0.0', 'onstructive-micronaut-commons-1.0.0.pom')

        then:
        noExceptionThrown()

        and:
        res.status == HttpStatus.OK
        res.contentType.get() == artifact.mediaType()
    }

    void "Request artifact anonymously"() {

        when:
        artifactClientAnonymous.readArtifact('mavenCentral', 'ch.onstructive', 'onstructive-micronaut-commons', '1.0.0', 'onstructive-micronaut-commons-1.0.0.pom')

        then:
        HttpClientResponseException ex = thrown(HttpClientResponseException)

        and:
        ex.status == HttpStatus.UNAUTHORIZED

        and: 'no mock was called'
        0 * _
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
