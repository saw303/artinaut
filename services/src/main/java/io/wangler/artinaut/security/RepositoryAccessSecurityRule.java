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
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
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

  public RepositoryAccessSecurityRule(RolesFinder rolesFinder) {
    super(rolesFinder);
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

    // TODO implement the business logic here in a non blocking manner.
    /*
    1. Load the repo & the current user
    2. Verify if the repo is assigned one of the users groups.
    */

    return Mono.just(SecurityRuleResult.REJECTED);
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
