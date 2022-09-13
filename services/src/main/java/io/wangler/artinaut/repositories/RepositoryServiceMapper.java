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

import ch.onstructive.mapping.mapstruct.MicronautMappingConfig;
import io.wangler.artinaut.LocalRepository;
import io.wangler.artinaut.RemoteRepository;
import io.wangler.artinaut.Repository;
import io.wangler.artinaut.VirtualRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MicronautMappingConfig.class)
public interface RepositoryServiceMapper {
  default RepositoryDto toRepositoryDto(Repository repository) {

    if (repository instanceof LocalRepository lr) {
      return toRepositoryDto(lr);
    } else if (repository instanceof RemoteRepository rr) {
      return toRepositoryDto(rr);
    } else if (repository instanceof VirtualRepository vr) {
      return toRepositoryDto(vr);
    } else {
      throw new RuntimeException(
          "Unknown repository class " + repository.getClass().getCanonicalName());
    }
  }

  LocalRepositoryDto toRepositoryDto(LocalRepository repository);

  RemoteRepositoryDto toRepositoryDto(RemoteRepository repository);

  VirtualRepositoryDto toRepositoryDto(VirtualRepository repository);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "groups", ignore = true)
  RemoteRepository fromRemoteRepository(RemoteRepositoryDto remoteRepository);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "groups", ignore = true)
  LocalRepository fromlocalRepositoryDto(LocalRepositoryDto localRepository);
}
