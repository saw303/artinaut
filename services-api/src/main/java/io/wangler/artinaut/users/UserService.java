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
import java.util.Optional;

public interface UserService {

  /**
   * Verifies whether the a user with that username exists.
   *
   * @param username the users name
   * @return yes or no
   */
  boolean userExists(String username);

  /**
   * Creates an standard user. The user gets the role «user» assigned.
   *
   * @param username the users name
   * @return the user dto
   * @throws UserExistsException if the user already exists
   */
  UserDto createUser(String username);

  /**
   * Creates an admin user. The user gets the role «admin» assigned.
   *
   * @param username the users name
   * @return the user dto
   * @throws UserExistsException if the user already exists
   */
  UserDto createAdminUser(String username);

  /**
   * Reads the user.
   *
   * @param username the users name
   * @return potential user
   */
  Optional<UserDto> findUser(String username);

  boolean canAccess(String repoKey, Authentication authentication);
}
