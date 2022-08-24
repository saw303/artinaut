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

import static io.micronaut.http.HttpRequest.GET;
import static io.micronaut.http.HttpStatus.OK;
import static io.micronaut.http.MediaType.ALL_TYPE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

import ch.onstructive.exceptions.NotFoundException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.uri.UriBuilder;
import io.wangler.artinaut.Artifact;
import io.wangler.artinaut.ArtifactContextDto;
import io.wangler.artinaut.ArtifactDto;
import io.wangler.artinaut.ArtifactRepository;
import io.wangler.artinaut.RemoteRepository;
import io.wangler.artinaut.RemoteRepositoryRepository;
import io.wangler.artinaut.Repository;
import io.wangler.artinaut.config.FileStoreConfig;
import jakarta.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class RemoteArtifactResolver extends BaseArtifactResolver implements ArtifactResolver {

  private final RemoteRepositoryRepository remoteRepositoryRepository;
  private final FileStoreConfig fileStoreConfig;
  private final ArtifactMapper artifactMapper;
  private final ArtifactRepository artifactRepository;

  public RemoteArtifactResolver(
      ArtifactRepository artifactRepository,
      FileStoreConfig fileStoreConfig,
      RemoteRepositoryRepository remoteRepositoryRepository,
      FileStoreConfig fileStoreConfig1,
      ArtifactMapper artifactMapper,
      ArtifactRepository artifactRepository1) {
    super(artifactRepository, fileStoreConfig);
    this.remoteRepositoryRepository = remoteRepositoryRepository;
    this.fileStoreConfig = fileStoreConfig1;
    this.artifactMapper = artifactMapper;
    this.artifactRepository = artifactRepository1;
  }

  @Override
  public boolean supports(Repository repository) {
    return repository instanceof RemoteRepository;
  }

  @Override
  @Transactional
  @SneakyThrows({IOException.class})
  public ArtifactDto resolveArtifact(ArtifactContextDto context) {

    RemoteRepository repository =
        remoteRepositoryRepository
            .findByKey(context.repositoryKey())
            .orElseThrow(() -> new NotFoundException("repository", context.repositoryKey()));

    Optional<ArtifactDto> artifactDto = resolveArtifactLocally(context, repository);

    if (artifactDto.isPresent()) {
      return artifactDto.get();
    }

    // request the artifact from remote repo
    final URL url = repository.getUrl();

    try (HttpClient httpClient = HttpClient.create(url)) {
      String uri =
          UriBuilder.of("{path}/{groupId}/{artifactId}/{version}/{filename}")
              .expand(
                  Map.of(
                      "path",
                      repository.getPath(),
                      "groupId",
                      context.groupId(),
                      "artifactId",
                      context.artifactId(),
                      "version",
                      context.version(),
                      "filename",
                      context.filename()))
              .toString();
      HttpResponse<byte[]> exchange = httpClient.toBlocking().exchange(GET(uri), byte[].class);

      if (exchange.getStatus() == OK && exchange.getBody().isPresent()) {
        log.debug("Found '{}' in repo '{}'", context.filename(), repository.getKey());

        byte[] buffer = exchange.getBody().get();
        if (repository.isStoreArtifactsLocally()) {
          Artifact artifact =
              artifactMapper.toArtifact(context, exchange.getContentType().get(), repository);

          log.debug("About to save artifact: «{}»", artifact);
          artifact = artifactRepository.save(artifact);

          Path path = fileStoreConfig.getPath().resolve(artifact.getId().toString());

          if (!Files.exists(path)) {
            Files.write(path, buffer, CREATE_NEW);
          }
        }
        return new ArtifactDto(
            exchange.getContentType().orElse(ALL_TYPE), new ByteArrayInputStream(buffer));
      } else {
        log.warn("Did not find '{}' in repo '{}'", context.filename(), repository.getKey());
        throw new NotFoundException("artifact", context.filename());
      }
    }
  }
}
