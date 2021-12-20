/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.ccaedio.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link CCAEdioBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public class CCAEdioBindingConstants {

    private static final String BINDING_ID = "ccaedio";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ACCOUNT = new ThingTypeUID(BINDING_ID, "account");
    public static final ThingTypeUID THING_TYPE_CCA = new ThingTypeUID(BINDING_ID, "cca");
    public static final ThingTypeUID THING_TYPE_STUDENT = new ThingTypeUID(BINDING_ID, "student");

    // List of all thing property types
    public static final String ACCOUNT_PROPERTY_USERNAME = "username";
    public static final String ACCOUNT_PROPERTY_PASSWORD = "password";
    public static final String ACCOUNT_PROPERTY_REFRESH_INTERVAL = "refreshInterval";
    public static final String ACCOUNT_PROPERTY_DAYS_TO_RETRIEVE = "daysToRetrieve";
    public static final String STUDENT_PROPERTY_ID = "studentid";
    public static final String STUDENT_PROPERTY_NAME = "studentname";

    // List of all Channel ids
    public static final String CHANNEL_HAS_SCHOOL = "hasSchool";
    public static final String CHANNEL_OVERDUES = "overduesEvents";
    public static final String CHANNEL_UPCOMING = "upcomingEvents";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>(
            Arrays.asList(THING_TYPE_ACCOUNT, THING_TYPE_ACCOUNT, THING_TYPE_CCA, THING_TYPE_STUDENT));
}
