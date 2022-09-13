package io.wangler.artinaut.users

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.wangler.artinaut.ArtinautTestPropertyProvider
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class UserControllerSpec extends Specification implements ArtinautTestPropertyProvider {

    @Inject
    UserClient userClient;

    void "Create user Peter, modify and delete Peter again"() {

        given:
        def model = new UserOperations.UserPostModel('peter', 'peterpwd', ['READER'] as Set)

        when:
        UUID userId = userClient.createUser(model).getBody(UUID).get()

        then:
        noExceptionThrown()

        and:
        userId

        when:
        UserOperations.UserGetModel user = userClient.findUser(userId)

        then:
        with(user) {
            id() == userId
            name() == model.name()
            groups() == model.groups()
        }

        when:
        userClient.updateUser(userId, new UserOperations.UserPutModel(model.password(), ['ADMIN', 'READER'] as Set))

        and:
        user = userClient.findUser(userId)

        then:
        with(user) {
            id() == userId
            name() == model.name()
            groups().sort() == ['ADMIN', 'READER']
        }

        when:
        userClient.deleteUser(userId)

        then:
        userClient.findUser(userId) == null
    }
}
