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

import ch.onstructive.exceptions.NotFoundException;
import io.wangler.artinaut.ArtifactContextDto;
import io.wangler.artinaut.ArtifactDto;
import io.wangler.artinaut.Repository;
import io.wangler.artinaut.VirtualRepository;
import io.wangler.artinaut.VirtualRepositoryRepository;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class VirtualArtifactResolver implements ArtifactResolver {

  private final VirtualRepositoryRepository virtualRepositoryRepository;
  private final Provider<ArtifactResolverFactory> artifactResolverFactory;
  private final VirtualArtifactResolverMapper virtualArtifactResolverMapper;

  @Override
  public boolean supports(Repository repository) {
    return repository instanceof VirtualRepository;
  }

  @Override
  @Transactional
  public Optional<ArtifactDto> resolveArtifact(ArtifactContextDto context) {

    VirtualRepository repository =
        virtualRepositoryRepository
            .findByKey(context.repositoryKey())
            .orElseThrow(() -> new NotFoundException("repository", context.repositoryKey()));

    for (Repository delegateRepository : repository.getRepositories()) {
      log.info("Delegating request to repo '{}'", delegateRepository.getKey());
      ArtifactResolver artifactResolver =
          artifactResolverFactory.get().resolveArtifactResolver(delegateRepository);
      ArtifactContextDto adoptedContext =
          virtualArtifactResolverMapper.copy(context, delegateRepository.getKey());

      Optional<ArtifactDto> artifactDto = artifactResolver.resolveArtifact(adoptedContext);

      if (artifactDto.isPresent()) {
        return artifactDto;
      }
      log.debug(
          "Artifact '{}:{}:{}' not found in repo '{}'",
          adoptedContext.toMavenizedGroupId(),
          adoptedContext.artifactId(),
          adoptedContext.version(),
          delegateRepository.getKey());
    }
    return Optional.empty();
  }
}
