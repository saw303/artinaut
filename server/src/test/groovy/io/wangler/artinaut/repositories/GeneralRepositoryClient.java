package io.wangler.artinaut.repositories;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;

@Client("/api/v1/repositories/")
@Header(name = HttpHeaders.AUTHORIZATION, value = "${artinaut.test.admin.basic-auth-header.value}")
public interface GeneralRepositoryClient extends GeneralRepositoryOperations {
}
