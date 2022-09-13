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

import io.wangler.artinaut.config.PasswordEncoderConfig;
import jakarta.inject.Singleton;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import lombok.SneakyThrows;

public sealed interface PasswordEncoder {

  String encode(CharSequence rawPassword);

  boolean matches(CharSequence rawPassword, String encodedPassword);

  @Singleton
  final class PBKDF2PasswordEncoder implements PasswordEncoder {

    private final SecretKeyFactory factory;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @SneakyThrows({NoSuchAlgorithmException.class})
    public PBKDF2PasswordEncoder(PasswordEncoderConfig passwordEncoderConfig) {
      this.passwordEncoderConfig = passwordEncoderConfig;
      this.factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    }

    @Override
    @SneakyThrows({InvalidKeySpecException.class})
    public String encode(CharSequence rawPassword) {
      KeySpec spec =
          new PBEKeySpec(
              rawPassword.toString().toCharArray(),
              this.passwordEncoderConfig.getSalt().getBytes(),
              this.passwordEncoderConfig.getIterationCount(),
              this.passwordEncoderConfig.getKeyLength());
      return toHex(factory.generateSecret(spec).getEncoded());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
      return Objects.equals(encodedPassword, encode(rawPassword));
    }

    private static String toHex(byte[] array) {
      BigInteger bi = new BigInteger(1, array);
      String hex = bi.toString(16);

      int paddingLength = (array.length * 2) - hex.length();
      if (paddingLength > 0) {
        return String.format("%0" + paddingLength + "d", 0) + hex;
      } else {
        return hex;
      }
    }
  }
}
