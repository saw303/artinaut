package io.wangler.artinaut


import io.micronaut.test.support.TestPropertyProvider
import io.wangler.artinaut.security.AuthenticationTestUtil

import static io.wangler.artinaut.security.AuthenticationTestUtil.basicAuthAdmin

interface ArtinautTestPropertyProvider extends TestPropertyProvider {

    @Override
    default Map<String, String> getProperties() {
        final String pwd = '$sam€$@m€'
        return [
                (AuthenticationTestUtil.KEY_BASE_AUTH)    : basicAuthAdmin(pwd),
                (AuthenticationTestUtil.KEY_BASE_AUTH_PWD): pwd
        ]
    }
}
