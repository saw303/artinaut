/*
 * MIT License
 * <p>
 * Copyright (c) 2020-2013 Silvio Wangler (silvio@wangler.io)
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
package io.wangler.artinaut.hibernate;

import java.util.Locale;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * @author Silvio Wangler (silvio.wangler@onstructive.ch)
 */
public class JpaPhysicalNamingStrategy implements PhysicalNamingStrategy {
  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }

  private Identifier apply(Identifier name, JdbcEnvironment jdbcEnvironment) {
    if (name == null) {
      return null;
    }
    StringBuilder builder = new StringBuilder(name.getText().replace('.', '_'));
    for (int i = 1; i < builder.length() - 1; i++) {
      if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
        builder.insert(i++, '_');
      }
    }
    return getIdentifier(builder.toString(), name.isQuoted(), jdbcEnvironment);
  }

  /**
   * Get an identifier for the specified details. By default this method will return an identifier
   * with the name adapted based on the result of {@link #isCaseInsensitive(JdbcEnvironment)}
   *
   * @param name the name of the identifier
   * @param quoted if the identifier is quoted
   * @param jdbcEnvironment the JDBC environment
   * @return an identifier instance
   */
  protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
    if (isCaseInsensitive(jdbcEnvironment)) {
      name = name.toLowerCase(Locale.ROOT);
    }
    return new Identifier(name, quoted);
  }

  /**
   * Specify whether the database is case sensitive.
   *
   * @param jdbcEnvironment the JDBC environment which can be used to determine case
   * @return true if the database is case insensitive sensitivity
   */
  protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
    return true;
  }

  private boolean isUnderscoreRequired(char before, char current, char after) {
    return Character.isLowerCase(before)
        && Character.isUpperCase(current)
        && Character.isLowerCase(after);
  }
}
