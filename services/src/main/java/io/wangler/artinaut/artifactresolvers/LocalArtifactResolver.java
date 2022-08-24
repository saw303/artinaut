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
import io.wangler.artinaut.ArtifactRepository;
import io.wangler.artinaut.LocalRepository;
import io.wangler.artinaut.LocalRepositoryRepository;
import io.wangler.artinaut.Repository;
import io.wangler.artinaut.config.FileStoreConfig;
import jakarta.inject.Singleton;

@Singleton
public class LocalArtifactResolver extends BaseArtifactResolver implements ArtifactResolver {

  private final LocalRepositoryRepository localRepositoryRepository;

  public LocalArtifactResolver(
      ArtifactRepository artifactRepository,
      FileStoreConfig fileStoreConfig,
      LocalRepositoryRepository localRepositoryRepository) {
    super(artifactRepository, fileStoreConfig);
    this.localRepositoryRepository = localRepositoryRepository;
  }

  @Override
  public boolean supports(Repository repository) {
    return repository instanceof LocalRepository;
  }

  @Override
  public ArtifactDto resolveArtifact(ArtifactContextDto context) {
    LocalRepository repository =
        localRepositoryRepository
            .findByKey(context.repositoryKey())
            .orElseThrow(() -> new NotFoundException("repository", context.repositoryKey()));

    return resolveArtifactLocally(context, repository)
        .orElseThrow(() -> new NotFoundException("artifact", context.artifactId()));
  }
}
