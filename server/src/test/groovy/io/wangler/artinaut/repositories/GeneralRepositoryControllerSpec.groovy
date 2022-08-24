package io.wangler.artinaut.repositories

import ch.onstructive.micronaut.test.MicronautDatabaseSpecification
import io.micronaut.http.HttpResponse
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject

import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE

@MicronautTest
class GeneralRepositoryControllerSpec extends MicronautDatabaseSpecification {

    @Inject
    GeneralRepositoryClient generalRepositoryClient


    void "List all registered repositories"() {
        when:
        List<GeneralRepositoryController.GeneralRepositoryGetModel> repositories = generalRepositoryClient.findAllRepositories()

        then:
        repositories.size() == 2

        when:
        GeneralRepositoryController.GeneralRepositoryGetModel testRemote = repositories.find { it.key() == 'test-remote'}
        GeneralRepositoryController.GeneralRepositoryGetModel testLocal = repositories.find { it.key() == 'test-local'}

        then:
        with(testLocal) {
            id()
            key() == 'test-local'
            type() == 'local'
        }

        and:
        with(testRemote) {
            id()
            key() == 'test-remote'
            type() == 'remote'
        }

        when:
        GeneralRepositoryController.RemoteRepositoryGetModel remoteRepo = generalRepositoryClient.findRemoteRepo(testRemote.id())

        then:
        with(remoteRepo) {
            id() == testRemote.id()
            key() == testRemote.key()
            url() == new URL('http://localhost:8090/')
            path() == 'repositories'
            storeArtifactsLocally() == TRUE
            handleReleases() == TRUE
            handleSnapshots() == TRUE
        }

        when:
        GeneralRepositoryController.LocalRepositoryGetModel localRepo = generalRepositoryClient.findLocalRepo(testLocal.id())

        then:
        with(localRepo) {
            id() == testLocal.id()
            key() == testLocal.key()
        }
    }

    void "Create a remote repository"() {

        given:
        GeneralRepositoryController.RemoteRepositoryPostModel myRepo = new GeneralRepositoryController.RemoteRepositoryPostModel(
                null, 'test-my-repo', new URL('http://repo.one.com/'), 'repos', 'user', 'pwd', FALSE, TRUE, FALSE
        )

        when:
        HttpResponse<UUID> res = generalRepositoryClient.addRemoteRepo(myRepo)

        and:
        UUID repoId = res.getBody(UUID).get()

        and:
        GeneralRepositoryController.RemoteRepositoryGetModel repo = generalRepositoryClient.findRemoteRepo(repoId)

        then:
        noExceptionThrown()

        and:
        repo.id()
    }
}
