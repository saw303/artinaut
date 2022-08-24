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

import io.wangler.artinaut.artifactresolvers.ArtifactResolver;
import io.wangler.artinaut.artifactresolvers.ArtifactResolverFactory;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class DefaultArtifactService implements ArtifactService {

  private final RepositoryRepository repositoryRepository;
  private final ArtifactResolverFactory artifactResolverFactory;

  @Override
  public ArtifactDto resolveArtifact(ArtifactContextDto context) {

    Repository repository =
        repositoryRepository
            .findByKey(context.repositoryKey())
            .orElseThrow(() -> new RepositoryDoesNotExistException(context.repositoryKey()));

    ArtifactResolver resolver = artifactResolverFactory.resolveArtifactResolver(repository);

    return resolver.resolveArtifact(context);
  }
}
