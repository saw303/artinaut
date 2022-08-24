package io.wangler.artinaut

import spock.lang.Specification

class ArtifactContextDtoSpec extends Specification {

    void "Mavenize the group id"() {

        given:
        ArtifactContextDto context = new ArtifactContextDto('repo1', 'com/hello', 'world', '1.0.0', 'world-1.0.0.pom')

        expect:
        context.toMavenizedGroupId() == 'com.hello'

        and:
        context.toType() == 'pom'
    }
}
