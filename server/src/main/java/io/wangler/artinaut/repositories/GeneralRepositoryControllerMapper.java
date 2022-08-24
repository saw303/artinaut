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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MicronautMappingConfig.class)
public interface GeneralRepositoryControllerMapper {
  default GeneralRepositoryController.GeneralRepositoryGetModel toGeneralRepositoryGetModel(
      RepositoryDto repositoryDto) {
    if (repositoryDto instanceof LocalRepositoryDto lrd) {
      return fromLocalRepositoryDto(lrd);
    } else if (repositoryDto instanceof VirtualRepositoryDto vrd) {
      return fromVirtualRepositoryDto(vrd);
    } else if (repositoryDto instanceof RemoteRepositoryDto rrd) {
      return fromRemoteRepositoryDto(rrd);
    } else {
      throw new RuntimeException("Unknown class " + repositoryDto.getClass().getCanonicalName());
    }
  }

  @Mapping(target = "type", constant = "local")
  GeneralRepositoryController.GeneralRepositoryGetModel fromLocalRepositoryDto(
      LocalRepositoryDto repositoryDto);

  @Mapping(target = "type", constant = "virtual")
  GeneralRepositoryController.GeneralRepositoryGetModel fromVirtualRepositoryDto(
      VirtualRepositoryDto repositoryDto);

  @Mapping(target = "type", constant = "remote")
  GeneralRepositoryController.GeneralRepositoryGetModel fromRemoteRepositoryDto(
      RemoteRepositoryDto repositoryDto);

  @Mapping(target = "type", constant = "remote")
  GeneralRepositoryController.RemoteRepositoryGetModel toRemoteRepositoryGetModel(
      RemoteRepositoryDto dto);

  @Mapping(target = "type", constant = "local")
  GeneralRepositoryController.LocalRepositoryGetModel toLocalRepositoryGetModel(
      LocalRepositoryDto dto);

  @Mapping(target = "type", constant = "virtual")
  GeneralRepositoryController.VirtualRepositoryGetModel toVirtualRepositoryGetModel(
      VirtualRepositoryDto dto);

  @Mapping(target = "id", ignore = true)
  RemoteRepositoryDto fromRemoteRepositoryPostModel(
      GeneralRepositoryController.RemoteRepositoryPostModel model);
}
