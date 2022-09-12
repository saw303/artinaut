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

import ch.onstructive.exceptions.NotFoundException;
import ch.onstructive.exceptions.NotYetImplementedException;
import io.micronaut.context.annotation.Property;
import io.micronaut.transaction.annotation.ReadOnly;
import io.wangler.artinaut.Group;
import io.wangler.artinaut.GroupRepository;
import io.wangler.artinaut.User;
import io.wangler.artinaut.UserRepository;
import io.wangler.artinaut.security.PasswordEncoder;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class DefaultUserService implements UserService {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  private CharSequence adminPassword;

  public DefaultUserService(
      UserRepository userRepository,
      GroupRepository groupRepository,
      PasswordEncoder passwordEncoder,
      UserMapper userMapper,
      @Property(name = "artinaut.test.admin.password") CharSequence adminPassword) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
    this.adminPassword = adminPassword;
  }

  @Override
  @ReadOnly
  public boolean userExists(String username) {
    return userRepository.existsByName(username);
  }

  @Override
  @Transactional
  public UserDto createUser(String username) {
    throw new NotYetImplementedException();
  }

  @Override
  @Transactional
  public UserDto createAdminUser(String username) {

    if (userExists(username)) {
      throw new UserExistsException(username);
    }

    Group adminGroup =
        groupRepository
            .findByName(Group.ADMIN_ROLE_NAME)
            .orElseThrow(() -> new NotFoundException("group", Group.ADMIN_ROLE_NAME));

    if (this.adminPassword == null) {
      this.adminPassword = UUID.randomUUID().toString();
    }

    log.info("Generated password for admin user «{}» is «{}»", username, this.adminPassword);

    User user =
        userRepository.save(
            new User(username, passwordEncoder.encode(this.adminPassword), Set.of(adminGroup)));
    return userMapper.toUserDto(user);
  }

  @Override
  @ReadOnly
  public Optional<UserDto> findUser(String username) {
    return userRepository.findByName(username).map(userMapper::toUserDto);
  }
}
