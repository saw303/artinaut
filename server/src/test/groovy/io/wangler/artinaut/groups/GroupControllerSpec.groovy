package io.wangler.artinaut.groups


import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.wangler.artinaut.ArtinautTestPropertyProvider
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class GroupControllerSpec extends Specification implements ArtinautTestPropertyProvider {

    @Inject
    GroupClient groupClient

    void "Verify available groups"() {

        when:
        List<GroupOperations.GroupGetModel> groups = groupClient.findAllGroups()

        then:
        groups.size() == 2

        and:
        groups*.name().sort() == ['ADMIN', 'READER']
    }

    void "Create a group superstars and delete it right away"() {

        given:
        final String GROUPNAME = 'SUPERSTARS'

        final short INITIAL_COUNT = 2

        when:
        UUID groupId = groupClient.createGroup(new GroupOperations.GroupPostModel(GROUPNAME)).getBody(UUID).get()

        and:
        List<GroupOperations.GroupGetModel> groups = groupClient.findAllGroups()

        then:
        groups.size() == INITIAL_COUNT + 1

        and:
        groups*.name().sort() == ['ADMIN', 'READER', GROUPNAME]

        and:
        groups.find { g -> g.name() == GROUPNAME }.id() == groupId

        when:
        groupClient.deleteGroup(groupId)

        and:
        groups = groupClient.findAllGroups()

        then:
        noExceptionThrown()

        and:
        groups.size() == INITIAL_COUNT

        and:
        groups.first().name() == 'ADMIN'
    }
}
