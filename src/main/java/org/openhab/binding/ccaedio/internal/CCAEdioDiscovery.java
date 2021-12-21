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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.ccaedio.internal.handler.AccountBridgeHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wolfecomputerservices.edioapi.objects.Student;

/**
 * The {@link CCAEdioHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public class CCAEdioDiscovery extends AbstractDiscoveryService implements ThingHandlerService {

    private @Nullable AccountBridgeHandler accountHandler;

    private final Logger logger;

    private @Nullable Future<?> discoveryJob;

    public CCAEdioDiscovery() {
        super(CCAEdioBindingConstants.SUPPORTED_THING_TYPES_UIDS, 10);
        this.logger = LoggerFactory.getLogger(getClass().getName());
    }

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        if (handler instanceof AccountBridgeHandler) {
            this.accountHandler = (AccountBridgeHandler) handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return accountHandler;
    }

    @Override
    public void activate() {
        super.activate(null);
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return CCAEdioBindingConstants.SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("Starting background discovery job");
        Future<?> localDiscoveryJob = discoveryJob;
        if (localDiscoveryJob == null || localDiscoveryJob.isCancelled()) {
            discoveryJob = scheduler.schedule(this::backgroundDiscover, 3, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stopping background discovery job");
        Future<?> localDiscoveryJob = discoveryJob;
        if (localDiscoveryJob != null) {
            localDiscoveryJob.cancel(true);
            discoveryJob = null;
        }
    }

    @Override
    protected void startScan() {
        logger.debug("Starting discovery scan");
        discover();
    }

    private void backgroundDiscover() {
        AccountBridgeHandler localHandler = accountHandler;
        if (localHandler != null) {
            if (localHandler.getThing().getStatus() != ThingStatus.ONLINE) {
                logger.debug("Skipping discovery because bridge is not ONLINE");
                return;
            }
        }
        discover();
    }

    private synchronized void discover() {
        logger.debug("Discovering students");
        AccountBridgeHandler localHandler = accountHandler;
        if (localHandler != null) {
            for (Student student : localHandler.getEdio().getStudents()) {
                int studentId = student.getId();
                String studentName = student.getName();
                logger.debug("Student [{}: {}] discovered.", studentId, studentName);
                @Nullable
                ThingUID bridgeThingUID = localHandler.getThing().getUID();

                ThingUID thingUID = new ThingUID(CCAEdioBindingConstants.THING_TYPE_STUDENT, bridgeThingUID,
                        String.valueOf(studentId));
                thingDiscovered(createStudentDiscoveryResult(thingUID, studentId, studentName));
            }
        }
    }

    private DiscoveryResult createStudentDiscoveryResult(ThingUID studentUID, int studentId, String studentName) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(CCAEdioBindingConstants.STUDENT_PROPERTY_ID, studentId);
        properties.put(CCAEdioBindingConstants.STUDENT_PROPERTY_NAME, studentName);
        DiscoveryResultBuilder drb = DiscoveryResultBuilder.create(studentUID).withProperties(properties)
                .withRepresentationProperty(CCAEdioBindingConstants.STUDENT_PROPERTY_ID).withLabel(studentName);
        AccountBridgeHandler localHandler = accountHandler;
        if (localHandler != null) {
            drb.withBridge(localHandler.getThing().getUID());
        }

        return drb.build();
    }
}
