package io.wangler.artinaut.repositories

import ch.onstructive.micronaut.test.MicronautDatabaseSpecification
import io.micronaut.http.HttpResponse
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.wangler.artinaut.ArtinautTestPropertyProvider
import jakarta.inject.Inject

import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE

@MicronautTest
class GeneralRepositoryControllerSpec extends MicronautDatabaseSpecification implements ArtinautTestPropertyProvider {

    @Inject
    GeneralRepositoryClient generalRepositoryClient

    void "List all registered repositories"() {
        when:
        List<GeneralRepositoryOperations.GeneralRepositoryGetModel> repositories = generalRepositoryClient.findAllRepositories()

        then:
        repositories.size() == 2

        when:
        GeneralRepositoryOperations.GeneralRepositoryGetModel testRemote = repositories.find { it.key() == 'test-remote'}
        GeneralRepositoryOperations.GeneralRepositoryGetModel testLocal = repositories.find { it.key() == 'test-local'}

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
        GeneralRepositoryOperations.RemoteRepositoryGetModel remoteRepo = generalRepositoryClient.findRemoteRepo(testRemote.id())

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
        GeneralRepositoryOperations.LocalRepositoryGetModel localRepo = generalRepositoryClient.findLocalRepo(testLocal.id())

        then:
        with(localRepo) {
            id() == testLocal.id()
            key() == testLocal.key()
        }
    }

    void "Create a remote repository"() {

        given:
        GeneralRepositoryOperations.RemoteRepositoryPostModel myRepo = new GeneralRepositoryOperations.RemoteRepositoryPostModel(
                null, 'test-my-repo', new URL('http://repo.one.com/'), 'repos', 'user', 'pwd', FALSE, TRUE, FALSE
        )

        when:
        HttpResponse<UUID> res = generalRepositoryClient.addRemoteRepo(myRepo)

        and:
        UUID repoId = res.getBody(UUID).get()

        and:
        GeneralRepositoryOperations.RemoteRepositoryGetModel repo = generalRepositoryClient.findRemoteRepo(repoId)

        then:
        noExceptionThrown()

        and:
        with(repo) {
            id() == repoId
            key() == myRepo.key()
            url() == myRepo.url()
            path() == myRepo.path()
            username() == myRepo.username()
            password() == myRepo.password()
            handleSnapshots() == myRepo.handleSnapshots()
            handleReleases() == myRepo.handleReleases()
            storeArtifactsLocally() == myRepo.storeArtifactsLocally()
        }
    }

    void "Create a local repository"() {

        given:
        GeneralRepositoryOperations.LocalRepositoryPostModel myRepo = new GeneralRepositoryOperations.LocalRepositoryPostModel(
                null, 'test-my-repo', FALSE, TRUE
        )

        when:
        HttpResponse<UUID> res = generalRepositoryClient.addLocalRepo(myRepo)

        and:
        UUID repoId = res.getBody(UUID).get()

        and:
        GeneralRepositoryOperations.LocalRepositoryGetModel repo = generalRepositoryClient.findLocalRepo(repoId)

        then:
        noExceptionThrown()

        and:
        with(repo) {
            id() == repoId
            key() == myRepo.key()
            handleSnapshots() == myRepo.handleSnapshots()
            handleReleases() == myRepo.handleReleases()
        }
    }
}
