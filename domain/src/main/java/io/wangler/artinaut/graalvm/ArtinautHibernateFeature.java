/*
 * MIT License
 * <p>
 * Copyright (c) 2020-2020 Silvio Wangler (silvio@wangler.io)
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
package io.wangler.artinaut.graalvm;

import antlr.CommonToken;
import com.oracle.svm.core.annotate.AutomaticFeature;
import io.micronaut.core.graal.AutomaticFeatureUtils;
import io.wangler.artinaut.hibernate.JpaImplicitNamingStrategy;
import io.wangler.artinaut.hibernate.JpaPhysicalNamingStrategy;
import io.wangler.artinaut.hibernate.UuidUserType;
import org.graalvm.nativeimage.hosted.Feature;
import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;
import org.hibernate.sql.ordering.antlr.NodeSupport;
import org.hibernate.sql.ordering.antlr.OrderByFragment;
import org.hibernate.sql.ordering.antlr.OrderingSpecification;
import org.hibernate.sql.ordering.antlr.SortKey;
import org.hibernate.sql.ordering.antlr.SortSpecification;

/** Anything Artinaut Domain related that should be known by the GraalVM. */
@AutomaticFeature
final class ArtinautHibernateFeature implements Feature {

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {

    AutomaticFeatureUtils.registerClassForRuntimeReflectionAndReflectiveInstantiation(
        access, JpaImplicitNamingStrategy.class.getCanonicalName());
    AutomaticFeatureUtils.registerClassForRuntimeReflectionAndReflectiveInstantiation(
        access, JpaPhysicalNamingStrategy.class.getCanonicalName());

    AutomaticFeatureUtils.registerClassForRuntimeReflectionAndReflectiveInstantiation(
        access, UuidUserType.class.getCanonicalName());

    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, JoinedSubclassEntityPersister.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, SingleTableEntityPersister.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, UnionSubclassEntityPersister.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, OneToManyPersister.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, BasicCollectionPersister.class.getCanonicalName());

    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, NodeSupport.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, SortKey.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, OrderingSpecification.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, SortSpecification.class.getCanonicalName());
    AutomaticFeatureUtils.registerClassForRuntimeReflectionAndReflectiveInstantiation(
        access, OrderByFragment.class.getCanonicalName());
    AutomaticFeatureUtils.registerConstructorsForRuntimeReflection(
        access, CommonToken.class.getCanonicalName());
  }
}
