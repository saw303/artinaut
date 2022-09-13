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

import static io.micronaut.scheduling.TaskExecutors.IO;

import ch.onstructive.exceptions.NotFoundException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.scheduling.annotation.ExecuteOn;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller("/api/v1/repositories/")
public class GeneralRepositoryController implements GeneralRepositoryOperations {

  private final RepositoryService repositoryService;
  private final GeneralRepositoryControllerMapper mapper;

  @Override
  @ExecuteOn(IO)
  public List<GeneralRepositoryGetModel> findAllRepositories() {
    return StreamSupport.stream(repositoryService.findAllRepositories().spliterator(), false)
        .map(mapper::toGeneralRepositoryGetModel)
        .toList();
  }

  @Override
  @ExecuteOn(IO)
  public GeneralRepositoryGetModel findRepo(@PathVariable("id") UUID id) {
    return repositoryService
        .findRepository(id)
        .map(mapper::toGeneralRepositoryGetModel)
        .orElseThrow(() -> new NotFoundException("repository", id));
  }

  @Override
  @ExecuteOn(IO)
  public RemoteRepositoryGetModel findRemoteRepo(UUID id) {
    return mapper.toRemoteRepositoryGetModel(fetchRepository(id, RemoteRepositoryDto.class));
  }

  @Override
  @ExecuteOn(IO)
  public LocalRepositoryGetModel findLocalRepo(UUID id) {
    return mapper.toLocalRepositoryGetModel(fetchRepository(id, LocalRepositoryDto.class));
  }

  @Override
  @ExecuteOn(IO)
  public VirtualRepositoryGetModel findVirtualRepo(UUID id) {
    return mapper.toVirtualRepositoryGetModel(fetchRepository(id, VirtualRepositoryDto.class));
  }

  @Override
  @ExecuteOn(IO)
  public HttpResponse<UUID> addRemoteRepo(RemoteRepositoryPostModel model) {
    RepositoryDto remoteRepository =
        repositoryService.createRemoteRepository(mapper.fromRemoteRepositoryPostModel(model));
    return HttpResponse.created(remoteRepository.getId());
  }

  @Override
  @ExecuteOn(IO)
  public HttpResponse<UUID> addLocalRepo(LocalRepositoryPostModel model) {
    RepositoryDto remoteRepository =
        repositoryService.createLocalRepository(mapper.fromLocalRepositoryPostModel(model));
    return HttpResponse.created(remoteRepository.getId());
  }

  @Override
  @ExecuteOn(IO)
  public List<GroupGetModel> findAssignedGroups(UUID id) {
    RepositoryDto repository =
        repositoryService
            .findRepository(id)
            .orElseThrow(() -> new NotFoundException("repository", id));
    return mapper.toGroupGetModels(repository.getGroups());
  }

  @Override
  @ExecuteOn(IO)
  public HttpResponse<?> assignGroup(UUID id, UUID groupId) {
    repositoryService.assignGroup(id, groupId);
    return HttpResponse.accepted();
  }

  @Override
  @ExecuteOn(IO)
  public void unassignGroup(UUID id, UUID groupId) {
    repositoryService.unassignGroup(id, groupId);
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
}
