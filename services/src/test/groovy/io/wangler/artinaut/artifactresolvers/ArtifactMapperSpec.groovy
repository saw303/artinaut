package io.wangler.artinaut.artifactresolvers


import io.wangler.artinaut.Artifact
import io.wangler.artinaut.ArtifactContextDto
import io.wangler.artinaut.LocalRepository
import io.wangler.artinaut.Repository
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static io.micronaut.http.MediaType.APPLICATION_HAL_JSON_TYPE

class ArtifactMapperSpec extends Specification {

    @Subject
    ArtifactMapper artifactMapper = new ArtifactMapperImpl()

    @Unroll
    void "Map #source to #target"() {

        given:
        ArtifactContextDto context = new ArtifactContextDto('huhu', 'io.wangler', 'jello', '9.0.0', source)

        and:
        Repository repository = new LocalRepository()

        and:
        Artifact artifact = artifactMapper.toArtifact(context, APPLICATION_HAL_JSON_TYPE, repository)

        expect:
        with(artifact) {
            !id
            !version
            groupId == 'io.wangler'
            artifactId == 'jello'
            artifactVersion == '9.0.0'
            type == target
        }

        and:
        artifact.repositories.size() == 1
        artifact.repositories.contains(repository)

        where:
        source                 || target
        'jello-9.0.0.jar'      || 'jar'
        'jello-9.0.0.zip'      || 'zip'
        'jello-9.0.0.bzip2'    || 'bzip2'
        'jello-9.0.0.docx'     || 'docx'
        'jello-9.0.0.bat.docx' || 'bat.docx'
    }
}
