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
package io.wangler.artinaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.wangler.artinaut.config.ArtinautConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
  public static void main(String[] args) {
    ApplicationContext applicationContext = Micronaut.run(Application.class, args);

    ArtinautConfig config = applicationContext.getBean(ArtinautConfig.class);

    Path fileStore = config.getFileStore().getPath();
    if (!Files.exists(fileStore)) {
      System.out.println("path does not exist " + fileStore.toAbsolutePath());
      System.exit(-400);
    }

    if (!Files.isDirectory(fileStore)) {
      System.out.println("path does not a directory " + fileStore.toAbsolutePath());
      System.exit(-401);
    }

    if (!Files.isReadable(fileStore)) {
      System.out.println("path is not readable " + fileStore.toAbsolutePath());
      System.exit(-402);
    }

    if (!Files.isWritable(fileStore)) {
      System.out.println("path is not writable " + fileStore.toAbsolutePath());
      System.exit(-403);
    }
  }
}
