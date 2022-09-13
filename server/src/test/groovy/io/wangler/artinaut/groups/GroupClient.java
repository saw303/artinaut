package io.wangler.artinaut.groups;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;

@Client("/api/v1/groups/")
@Header(name = HttpHeaders.AUTHORIZATION, value = "${artinaut.test.admin.basic-auth-header.value}")
public interface GroupClient extends GroupOperations {
}
