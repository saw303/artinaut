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
package io.wangler.artinaut.security;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.wangler.artinaut.users.GroupDto;
import io.wangler.artinaut.users.RoleDto;
import io.wangler.artinaut.users.UserDto;
import io.wangler.artinaut.users.UserService;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class ArtinautAuthenticationProvider implements AuthenticationProvider {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Publisher<AuthenticationResponse> authenticate(
      HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
    return Flux.create(
        emitter -> {
          String identity = (String) authenticationRequest.getIdentity();
          UserDto user = userService.findUser(identity).orElse(null);

          if (user != null) {

            if (passwordEncoder.matches(
                (String) authenticationRequest.getSecret(), user.password())) {

              final Set<String> roles =
                  user.groups().stream()
                      .map(GroupDto::roles)
                      .flatMap(Collection::stream)
                      .map(RoleDto::name)
                      .collect(Collectors.toSet());
              emitter.next(AuthenticationResponse.success(identity, roles));
            } else {
              log.debug(
                  "Password does not match for user «{}» (user id «{}»)", identity, user.id());
              emitter.next(AuthenticationResponse.failure());
            }

          } else {
            emitter.next(AuthenticationResponse.failure());
          }
          emitter.complete();
        },
        FluxSink.OverflowStrategy.ERROR);
  }
}
