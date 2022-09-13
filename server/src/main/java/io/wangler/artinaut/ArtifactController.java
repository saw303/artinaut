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

import static io.micronaut.scheduling.TaskExecutors.IO;

import ch.onstructive.exceptions.NotFoundException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.validation.Validated;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller("/repos/{repoKey}/{groupId:.*}/{artifactId:.*}/{version:.*}/{filename}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ArtifactController {

  private final ArtifactService artifactService;
  private final ArtifactControllerMapper artifactControllerMapper;

  @Get
  @ExecuteOn(IO)
  public HttpResponse<?> get(
      @NotBlank @PathVariable("repoKey") String repositoryKey,
      @NotBlank String groupId,
      @NotBlank String artifactId,
      @NotBlank String version,
      @NotBlank String filename) {

    try {
      ArtifactDto artifact =
          artifactService.resolveArtifact(
              artifactControllerMapper.toArtifactContext(
                  repositoryKey, groupId, artifactId, version, filename));
      return HttpResponse.ok()
          .body(
              new StreamedFile(
                  artifact.inputStream(),
                  artifact.mediaType(),
                  artifact.lastModified(),
                  artifact.contentLength()));
    } catch (RepositoryDoesNotExistException ex) {
      log.error("repo «{}» not found", repositoryKey, ex);
      return HttpResponse.badRequest("repository «" + repositoryKey + "» does not exist");
    } catch (NotFoundException ex) {
      log.error("artifact not found", ex);
      return HttpResponse.notFound();
    }
  }
}
