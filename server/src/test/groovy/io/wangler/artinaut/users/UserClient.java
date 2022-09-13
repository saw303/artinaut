package io.wangler.artinaut.users;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;

@Client("/api/v1/users")
@Header(name = HttpHeaders.AUTHORIZATION, value = "${artinaut.test.admin.basic-auth-header.value}")
public interface UserClient extends UserOperations {
}
