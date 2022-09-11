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
package io.wangler.artinaut.artifactresolvers;

import io.micronaut.http.MediaType;
import io.wangler.artinaut.Artifact;
import io.wangler.artinaut.ArtifactContextDto;
import io.wangler.artinaut.ArtifactDto;
import io.wangler.artinaut.ArtifactRepository;
import io.wangler.artinaut.Repository;
import io.wangler.artinaut.config.FileStoreConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseArtifactResolver {

  private final ArtifactRepository artifactRepository;
  private final FileStoreConfig fileStoreConfig;

  @SneakyThrows({IOException.class})
  public Optional<ArtifactDto> resolveArtifactLocally(
      ArtifactContextDto context, Repository repository) {
    Optional<Artifact> potentialCachedArtefact =
        artifactRepository.findByGroupIdAndArtifactIdAndArtifactVersionAndTypeAndRepository(
            context.toMavenizedGroupId(),
            context.artifactId(),
            context.version(),
            context.toType(),
            repository);

    // serve cached artifact
    if (potentialCachedArtefact.isPresent()) {
      Artifact artifact = potentialCachedArtefact.get();
      log.info("Found cached artifact «{}» in repo «{}»", artifact, repository.getKey());

      Path path = fileStoreConfig.getPath().resolve(artifact.getId().toString());

      if (Files.exists(path)) {
        log.debug("Returning file «{}»", path);
        return Optional.of(
            new ArtifactDto(
                MediaType.of(artifact.getMediaType()),
                Files.newInputStream(path),
                Files.getLastModifiedTime(path).toMillis(),
                Files.size(path)));
      } else {
        log.error(
            "Cached artifact «{}» does not exists in repo «{}» but is registered in database",
            artifact,
            repository.getKey());
      }
    }
    return Optional.empty();
  }
}
