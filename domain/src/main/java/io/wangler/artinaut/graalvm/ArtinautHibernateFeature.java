/*
 * Copyright by onstructive GmbH 2020 - 2020-2022. All rights reserved.
 *
 * onstructive GmbH
 * Buckhauserstrasse 49
 * 8048 Zürich
 * Switzerland
 *
 * Unauthorized copying of this file via any medium is strictly prohibited.
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
