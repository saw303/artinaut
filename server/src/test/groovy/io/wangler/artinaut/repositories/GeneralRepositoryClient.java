package io.wangler.artinaut.repositories;

import io.micronaut.http.client.annotation.Client;

@Client("/api/v1/repositories/")
public interface GeneralRepositoryClient extends GeneralRepositoryOperations {
}
