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

import io.micronaut.security.authentication.Authentication;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserService {

  /**
   * Verifies whether the a user with that username exists.
   *
   * @param username the users name
   * @return yes or no
   */
  boolean userExists(String username);

  /**
   * Creates an standard user.
   *
   * @param username the users name
   * @param password users password
   * @param groups groups
   * @return the use dto
   */
  UserDto createUser(String username, String password, Set<String> groups);

  /**
   * Creates an admin user. The user gets the role «admin» assigned.
   *
   * @param username the users name
   * @return the user dto
   * @throws UserExistsException if the user already exists
   */
  UserDto createAdminUser(String username);

  UserDto createAdminUser(String username, String password);

  /**
   * Reads the user.
   *
   * @param username the users name
   * @return potential user
   */
  Optional<UserDto> findUser(String username);

  boolean canAccess(String repoKey, Authentication authentication);

  Optional<UserDto> findUser(UUID id);

  List<UserDto> findUsers();

  void deleteUser(UUID id);

  UserDto updateUser(UUID id, String password, Set<String> groups);
}
