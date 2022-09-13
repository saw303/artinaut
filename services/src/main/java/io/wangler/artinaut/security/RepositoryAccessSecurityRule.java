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
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.AbstractSecurityRule;
import io.micronaut.security.rules.ConfigurationInterceptUrlMapRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.security.token.RolesFinder;
import io.micronaut.web.router.RouteMatch;
import io.wangler.artinaut.users.UserService;
import jakarta.inject.Singleton;
import java.util.Map;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Verifies whether a user is allowed to access the given repository.
 *
 * @author Silvio Wangler <silvio.wangler@onstuctive.ch>
 * @since 0.2.0
 */
@Singleton
public class RepositoryAccessSecurityRule extends AbstractSecurityRule {

  public static final Integer ORDER = ConfigurationInterceptUrlMapRule.ORDER - 1;
  private static final String REPO_KEY = "repoKey";
  private final UserService userService;

  public RepositoryAccessSecurityRule(RolesFinder rolesFinder, UserService userService) {
    super(rolesFinder);
    this.userService = userService;
  }

  @Override
  public Publisher<SecurityRuleResult> check(
      HttpRequest<?> request, RouteMatch<?> routeMatch, Authentication authentication) {

    if (!request.getPath().startsWith("/repos/")) {
      return Mono.just(SecurityRuleResult.UNKNOWN);
    }

    if (authentication == null) {
      return Mono.just(SecurityRuleResult.REJECTED);
    }

    Map<String, Object> variableValues = routeMatch.getVariableValues();

    if (!variableValues.containsKey(REPO_KEY)) {
      return Mono.just(SecurityRuleResult.REJECTED);
    }

    final String repoKey = (String) variableValues.get(REPO_KEY);

    return Flux.create(
        emitter -> {
          if (userService.canAccess(repoKey, authentication)) {
            emitter.next(SecurityRuleResult.ALLOWED);
          } else {
            emitter.next(SecurityRuleResult.REJECTED);
          }
          emitter.complete();
        });
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
