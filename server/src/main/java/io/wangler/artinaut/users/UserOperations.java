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
package io.wangler.artinaut.users;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.validation.Validated;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
public interface UserOperations {

  @Get
  List<UserGetModel> findAllUsers();

  @Get("/{id}")
  UserGetModel findUser(@NotNull UUID id);

  @Delete("/{id}")
  void deleteUser(@NotNull UUID id);

  @Post
  HttpResponse<UUID> createUser(@Body @Valid UserPostModel model);

  @Put("/{id}")
  HttpResponse<UUID> updateUser(@NotNull UUID id, @Body @Valid UserPutModel model);

  @Introspected
  record UserGetModel(UUID id, String name, Set<String> groups) {}

  @Introspected
  record UserPostModel(
      @NotBlank String name, @NotBlank String password, @NotEmpty Set<String> groups) {}

  @Introspected
  record UserPutModel(@NotBlank String password, @NotEmpty Set<String> groups) {}
}
