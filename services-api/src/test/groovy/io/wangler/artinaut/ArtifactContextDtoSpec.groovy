package io.wangler.artinaut

import spock.lang.Specification

class ArtifactContextDtoSpec extends Specification {

    void "Mavenize the group id and verify type to be «#type»"() {

        given:
        ArtifactContextDto context = new ArtifactContextDto('repo1', 'com/hello', artifactId, version, "${artifactId}-${version}.${type}")

        expect:
        context.toMavenizedGroupId() == 'com.hello'

        and:
        context.toType() == type

        where:
        artifactId | version || type
        'world'    | '1.0.0' || 'pom'
        'moneta'   | '1.4.2' || 'pom.sha1'
        'moneta'   | '1.4.2' || 'jar.sha1'
    }
}
