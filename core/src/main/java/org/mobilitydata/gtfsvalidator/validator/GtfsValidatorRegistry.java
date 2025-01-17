package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableList;

/**
 * An interface to find all GTFS validator classes.
 *
 * <p>An implementation of this interface ({@code DefaultValidatorRegistry}) is generated by the
 * annotation processor.
 */
public interface GtfsValidatorRegistry {
  /** Returns all the registered and generated validator classes. */
  ImmutableList<Class<?>> getValidatorClasses();
}
