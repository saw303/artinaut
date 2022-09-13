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

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.validation.Validated;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
public interface GeneralRepositoryOperations {
  @Get
  List<GeneralRepositoryController.GeneralRepositoryGetModel> findAllRepositories();

  @Get("/{id}")
  GeneralRepositoryController.GeneralRepositoryGetModel findRepo(@PathVariable("id") UUID id);

  @Get("/remote/{id}")
  GeneralRepositoryController.RemoteRepositoryGetModel findRemoteRepo(@PathVariable("id") UUID id);

  @Get("/local/{id}")
  GeneralRepositoryController.LocalRepositoryGetModel findLocalRepo(@PathVariable("id") UUID id);

  @Get("/virtual/{id}")
  GeneralRepositoryController.VirtualRepositoryGetModel findVirtualRepo(
      @PathVariable("id") UUID id);

  @Post("/remote/")
  HttpResponse<UUID> addRemoteRepo(
      @Body @Valid GeneralRepositoryController.RemoteRepositoryPostModel model);

  @Post("/local/")
  HttpResponse<UUID> addLocalRepo(
      @Body @Valid GeneralRepositoryController.LocalRepositoryPostModel model);

  @Get("/{id}/groups/")
  List<GeneralRepositoryController.GroupGetModel> findAssignedGroups(UUID id);

  @Put("/{id}/groups/{groupId}")
  HttpResponse<?> assignGroup(UUID id, UUID groupId);

  @Delete("/{id}/groups/{groupId}")
  void unassignGroup(UUID id, UUID groupId);

  @Introspected
  record GeneralRepositoryGetModel(
      UUID id, String key, String type, Boolean handleReleases, Boolean handleSnapshots) {}

  @Introspected
  record LocalRepositoryGetModel(
      UUID id, String key, String type, Boolean handleReleases, Boolean handleSnapshots) {}

  @Introspected
  record VirtualRepositoryGetModel(
      UUID id, String key, String type, Boolean handleReleases, Boolean handleSnapshots) {}

  @Introspected
  record RemoteRepositoryGetModel(
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
  record RemoteRepositoryPostModel(
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
  record LocalRepositoryPostModel(
      UUID id,
      @NotBlank String key,
      @NotNull Boolean handleReleases,
      @NotNull Boolean handleSnapshots) {}

  @Introspected
  record GroupGetModel(UUID id, String name) {}
}
