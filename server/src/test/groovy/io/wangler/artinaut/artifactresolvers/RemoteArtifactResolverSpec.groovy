package io.wangler.artinaut.artifactresolvers


import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.wangler.artinaut.ArtifactContextDto
import io.wangler.artinaut.ArtifactDto
import io.wangler.artinaut.ArtifactRepository
import io.wangler.artinaut.RemoteRepository
import io.wangler.artinaut.RemoteRepositoryRepository
import jakarta.inject.Inject
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import spock.lang.Specification
import spock.lang.Subject

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE
import static io.micronaut.http.MediaType.APPLICATION_OCTET_STREAM
import static io.micronaut.http.MediaType.APPLICATION_OCTET_STREAM_TYPE
import static io.micronaut.http.MediaType.TEXT_XML
import static io.micronaut.http.MediaType.TEXT_XML_TYPE

@MicronautTest
class RemoteArtifactResolverSpec extends Specification {

    @Subject
    @Inject
    RemoteArtifactResolver resolver

    @Inject
    RemoteRepositoryRepository remoteRepositoryRepository

    @Inject
    ArtifactRepository artifactRepository

    MockWebServer server = new MockWebServer()

    void setup() {
        server.start()
        RemoteRepository remoteRepository = new RemoteRepository(
                key: 'test',
                url: new URL("http://localhost:${server.port}/"),
                path: 'maven',
                storeArtifactsLocally: true
        )
        remoteRepositoryRepository.save(remoteRepository)
    }

    void cleanup() {
        server.shutdown()
    }

    void "Test remote repository"() {

        given:
        ArtifactContextDto context = new ArtifactContextDto('test', 'io.wangler', 'test', '1.0.0', 'test-1.0.0.pom')

        and:
        server.enqueue(new MockResponse().setBody("123456").setHeader(CONTENT_TYPE, TEXT_XML))

        when:
        Optional<ArtifactDto> artifact = resolver.resolveArtifact(context)

        then:
        artifact.isPresent()

        and:
        with(artifact.get()) {
            mediaType() == TEXT_XML_TYPE
            inputStream()
        }

        and:
        artifactRepository.count() == 1

        and:
        artifactRepository.existsByGroupIdAndArtifactIdAndArtifactVersionAndType(context.groupId(), context.artifactId(), context.version(), 'pom')

        and:
        with(artifactRepository.findByGroupIdAndArtifactIdAndArtifactVersionAndType(context.groupId(), context.artifactId(), context.version(), 'pom').get()) {
            id
            version == 0
            groupId == context.groupId()
            artifactId == context.artifactId()
            artifactVersion == context.version()
            type == 'pom'
        }

        when:
        context = new ArtifactContextDto('test', 'io.wangler', 'test', '1.0.0', 'test-1.0.0.jar')

        and:
        server.enqueue(new MockResponse().setBody("123456").setHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM))

        and:
        artifact = resolver.resolveArtifact(context)

        then:
        artifact.isPresent()

        and:
        with(artifact.get()) {
            mediaType() == APPLICATION_OCTET_STREAM_TYPE
            inputStream()
        }

        and:
        artifactRepository.count() == 2

        and:
        artifactRepository.existsByGroupIdAndArtifactIdAndArtifactVersionAndType(context.groupId(), context.artifactId(), context.version(), 'jar')

        and:
        with(artifactRepository.findByGroupIdAndArtifactIdAndArtifactVersionAndType(context.groupId(), context.artifactId(), context.version(), 'jar').get()) {
            id
            version == 0
            groupId == context.groupId()
            artifactId == context.artifactId()
            artifactVersion == context.version()
            type == 'jar'
        }
    }
}
