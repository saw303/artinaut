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
package io.wangler.artinaut.repositories;

import ch.onstructive.exceptions.NotFoundException;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller("/api/v1/repositories/")
public class GeneralRepositoryController implements GeneralRepositoryOperations {

  private final RepositoryService repositoryService;
  private final GeneralRepositoryControllerMapper mapper;

  @Override
  public List<GeneralRepositoryGetModel> findAllRepositories() {
    return StreamSupport.stream(repositoryService.findAllRepositories().spliterator(), false)
        .map(mapper::toGeneralRepositoryGetModel)
        .toList();
  }

  @Override
  public GeneralRepositoryGetModel findRepo(@PathVariable("id") UUID id) {
    return repositoryService
        .findRepository(id)
        .map(mapper::toGeneralRepositoryGetModel)
        .orElseThrow(() -> new NotFoundException("repository", id));
  }

  @Override
  public RemoteRepositoryGetModel findRemoteRepo(UUID id) {
    return mapper.toRemoteRepositoryGetModel(fetchRepository(id, RemoteRepositoryDto.class));
  }

  @Override
  public LocalRepositoryGetModel findLocalRepo(UUID id) {
    return mapper.toLocalRepositoryGetModel(fetchRepository(id, LocalRepositoryDto.class));
  }

  @Override
  public VirtualRepositoryGetModel findVirtualRepo(UUID id) {
    return mapper.toVirtualRepositoryGetModel(fetchRepository(id, VirtualRepositoryDto.class));
  }

  private <T extends RepositoryDto> T fetchRepository(UUID id, Class<T> dtoClass) {
    RepositoryDto repository =
        repositoryService
            .findRepository(id)
            .orElseThrow(() -> new NotFoundException("repository", id));

    if (!repository.getClass().equals(dtoClass)) {
      throw new NotFoundException("repository", id);
    }
    return (T) repository;
  }

  @Override
  public HttpResponse<UUID> addRemoteRepo(RemoteRepositoryPostModel model) {
    RepositoryDto remoteRepository =
        repositoryService.createRemoteRepository(mapper.fromRemoteRepositoryPostModel(model));
    return HttpResponse.created(remoteRepository.getId());
  }

  @Override
  public HttpResponse addLocalRepo(LocalRepositoryPostModel model) {
    RepositoryDto remoteRepository =
        repositoryService.createLocalRepository(mapper.fromLocalRepositoryPostModel(model));
    return HttpResponse.created(remoteRepository.getId());
  }

  @Introspected
  public record GeneralRepositoryGetModel(
      UUID id, String key, String type, Boolean handleReleases, Boolean handleSnapshots) {}

  @Introspected
  public record LocalRepositoryGetModel(
      UUID id, String key, String type, Boolean handleReleases, Boolean handleSnapshots) {}

  @Introspected
  public record VirtualRepositoryGetModel(
      UUID id, String key, String type, Boolean handleReleases, Boolean handleSnapshots) {}

  @Introspected
  public record RemoteRepositoryGetModel(
      UUID id,
      String key,
      String type,
      URL url,
      String path,
      String username,
      String password,
      Boolean handleReleases,
      Boolean handleSnapshots,
      Boolean storeArtifactsLocally) {}

  @Introspected
  public record RemoteRepositoryPostModel(
      UUID id,
      @NotBlank String key,
      @NotNull URL url,
      @NotBlank String path,
      String username,
      String password,
      @NotNull Boolean handleReleases,
      @NotNull Boolean handleSnapshots,
      @NotNull Boolean storeArtifactsLocally) {}

  @Introspected
  public record LocalRepositoryPostModel(
      UUID id,
      @NotBlank String key,
      @NotNull Boolean handleReleases,
      @NotNull Boolean handleSnapshots) {}
}
