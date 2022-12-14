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
import io.micronaut.transaction.annotation.ReadOnly;
import io.wangler.artinaut.Group;
import io.wangler.artinaut.GroupRepository;
import io.wangler.artinaut.LocalRepository;
import io.wangler.artinaut.RemoteRepository;
import io.wangler.artinaut.Repository;
import io.wangler.artinaut.RepositoryRepository;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class DefaultRepositoryService implements RepositoryService {

  private final RepositoryRepository repositoryRepository;
  private final GroupRepository groupRepository;
  private final RepositoryServiceMapper repositoryServiceMapper;

  @Override
  @ReadOnly
  public Iterable<RepositoryDto> findAllRepositories() {
    return repositoryRepository.findAll().stream()
        .map(repositoryServiceMapper::toRepositoryDto)
        .toList();
  }

  @Override
  @ReadOnly
  public Optional<RepositoryDto> findRepository(UUID repoId) {
    return repositoryRepository.findById(repoId).map(repositoryServiceMapper::toRepositoryDto);
  }

  @Override
  @Transactional
  public RemoteRepositoryDto createRemoteRepository(RemoteRepositoryDto remoteRepository) {
    RemoteRepository repository = repositoryServiceMapper.fromRemoteRepository(remoteRepository);
    repositoryRepository.save(repository);

    return repositoryServiceMapper.toRepositoryDto(repository);
  }

  @Override
  @Transactional
  public LocalRepositoryDto createLocalRepository(LocalRepositoryDto localRepositoryDto) {
    LocalRepository repository = repositoryServiceMapper.fromlocalRepositoryDto(localRepositoryDto);
    repositoryRepository.save(repository);

    return repositoryServiceMapper.toRepositoryDto(repository);
  }

  @Override
  @Transactional
  public void unassignGroup(UUID repoId, UUID groupId) {
    Repository repository =
        repositoryRepository
            .findById(repoId)
            .orElseThrow(() -> new NotFoundException("repository", repoId));
    repository.getGroups().removeIf(g -> g.getId().equals(groupId));
    repositoryRepository.save(repository);
  }

  @Override
  @Transactional
  public void assignGroup(UUID repoId, UUID groupId) {

    Repository repository =
        repositoryRepository
            .findById(repoId)
            .orElseThrow(() -> new NotFoundException("repository", repoId));
    Group group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new NotFoundException("group", groupId));

    repository.getGroups().add(group);
    repositoryRepository.save(repository);
  }
}
