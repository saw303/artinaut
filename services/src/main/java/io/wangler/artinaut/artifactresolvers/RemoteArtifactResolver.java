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
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
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
  private final HttpClientConfiguration httpClientConfiguration =
      new DefaultHttpClientConfiguration();

  public RemoteArtifactResolver(
      ArtifactRepository artifactRepository,
      FileStoreConfig fileStoreConfig,
      RemoteRepositoryRepository remoteRepositoryRepository,
      ArtifactMapper artifactMapper) {
    super(artifactRepository, fileStoreConfig);
    this.remoteRepositoryRepository = remoteRepositoryRepository;
    this.fileStoreConfig = fileStoreConfig;
    this.artifactMapper = artifactMapper;
    this.artifactRepository = artifactRepository;
    this.httpClientConfiguration.setMaxContentLength(1024 * 1024 * 50); // 50 MiB
  }

  @Override
  public boolean supports(Repository repository) {
    return repository instanceof RemoteRepository;
  }

  @Override
  @Transactional
  @SneakyThrows({IOException.class})
  public Optional<ArtifactDto> resolveArtifact(ArtifactContextDto context) {

    RemoteRepository repository =
        remoteRepositoryRepository
            .findByKey(context.repositoryKey())
            .orElseThrow(() -> new NotFoundException("repository", context.repositoryKey()));

    Optional<ArtifactDto> artifactDto = resolveArtifactLocally(context, repository);

    if (artifactDto.isPresent()) {
      return artifactDto;
    }

    // request the artifact from remote repo
    final URL url = repository.getUrl();

    try (HttpClient httpClient = HttpClient.create(url, this.httpClientConfiguration)) {
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

      MutableHttpRequest<Object> request = GET(uri);

      if (repository.getUsername() != null && repository.getPassword() != null) {
        String usernamePassword =
            "%s:%s".formatted(repository.getUsername(), repository.getPassword());
        request =
            request.header(
                HttpHeaders.AUTHORIZATION,
                "Basic "
                    + Base64.getEncoder()
                        .encodeToString(usernamePassword.getBytes(StandardCharsets.UTF_8)));
      }

      try {
        HttpResponse<byte[]> response = httpClient.toBlocking().exchange(request, byte[].class);
        if (response.getStatus() == OK && response.getBody().isPresent()) {
          log.debug("Found «{}» in repo «{}»", context.filename(), repository.getKey());

          byte[] buffer = response.getBody().get();
          if (repository.isStoreArtifactsLocally()) {
            Artifact artifact =
                artifactMapper.toArtifact(context, response.getContentType().get(), repository);

            log.debug("About to save artifact: «{}»", artifact);
            artifact = artifactRepository.save(artifact);

            Path path = fileStoreConfig.getPath().resolve(artifact.getId().toString());

            if (!Files.exists(path)) {
              Files.write(path, buffer, CREATE_NEW);
            }
          }
          return Optional.of(
              new ArtifactDto(
                  response.getContentType().orElse(ALL_TYPE), new ByteArrayInputStream(buffer)));
        } else {
          log.warn(
              "Did not find «{}» in repo «{}» (http status: {})",
              context.filename(),
              repository.getKey(),
              response.getStatus().getCode());
          return Optional.empty();
        }
      } catch (HttpClientResponseException e) {
        log.error(
            "Failed to fetch «{}» in repo «{}» (http status: {})",
            context.filename(),
            repository.getKey(),
            e.getStatus().getCode());
        return Optional.empty();
      }
    }
  }
}
