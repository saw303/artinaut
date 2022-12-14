/*
 * MIT License
 * <p>
 * Copyright (c) 2022 Silvio Wangler (silvio@wangler.io)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 */
package io.wangler.artinaut;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, UUID> {

  Optional<Artifact> findByGroupIdAndArtifactIdAndArtifactVersionAndType(
      String groupId, String artifactId, String version, String type);

  boolean existsByGroupIdAndArtifactIdAndArtifactVersionAndType(
      String groupId, String artifactId, String version, String type);

  @Query(
      """
    select a
    from Artifact a, Repository r
    where a.groupId = :groupId
    and a.artifactId = :artifactId
    and a.artifactVersion = :version
    and a.type = :type
    and r in elements(a.repositories)
    and r = :repository
    """)
  Optional<Artifact> findByGroupIdAndArtifactIdAndArtifactVersionAndTypeAndRepository(
      String groupId,
      String artifactId,
      String version,
      String type,
      io.wangler.artinaut.Repository repository);
}
