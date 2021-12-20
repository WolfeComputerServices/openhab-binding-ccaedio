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
package org.openhab.binding.ccaedio.internal.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.ccaedio.internal.CCAEdioBindingConstants;
import org.openhab.binding.ccaedio.internal.EdioAPIBridge;
import org.openhab.channhandler.ChannelHandler;
import org.openhab.channhandler.ChannelHandlerOverdue;
import org.openhab.channhandler.ChannelHandlerUpcomingEvents;
import org.openhab.channhandler.ICCAThingHandler;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.wolfecomputerservices.edioapi.objects.Overdue;
import com.wolfecomputerservices.edioapi.objects.Upcoming;

/**
 * The {@link StudentHandler} is responsible for Student Thing handling.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public class StudentHandler extends BaseThingHandler implements ICCAThingHandler {
    private final Logger logger;
    private List<ChannelHandler> channelHandlers = new ArrayList<>();

    private final Gson gson;

    // private @Nullable ScheduledFuture<?> daily;

    public StudentHandler(Thing thing, Gson gson) {
        super(thing);

        logger = LoggerFactory.getLogger(getClass());

        this.gson = gson;
    }

    @Override
    public void update() {
        updateChannels();
    }

    @Override
    public Object getConfigProperty(String propertyName) {
        return getConfig().get(propertyName);
    }

    @Override
    public void initialize() {

        logger.debug("CCA Edio Handler initialization started");
        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.CONFIGURATION_ERROR, "PARENT BRIDGE NOT SELECTED");
        } else {
            AccountBridgeHandler accountHandler = (AccountBridgeHandler) bridge.getHandler();
            if (accountHandler != null) {
                EdioAPIBridge edioBridge = accountHandler.getEdio();
                channelHandlers.add(new ChannelHandlerOverdue(this, edioBridge, this.gson));
                channelHandlers.add(new ChannelHandlerUpcomingEvents(this, edioBridge, this.gson));
                updateStatus(ThingStatus.ONLINE);
                updateChannels();
            }
        }
    }

    private void updateChannels() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            AccountBridgeHandler account = (AccountBridgeHandler) bridge.getHandler();
            if (account != null) {
                EdioAPIBridge edioBridge = account.getEdio();
                String studentName = (String) getConfig().get(CCAEdioBindingConstants.STUDENT_PROPERTY_NAME);
                Overdue[] overdue = edioBridge.getOverdues(studentName);
                updateChannelState(CCAEdioBindingConstants.CHANNEL_OVERDUES, new StringType(
                        String.format("{ \"student\": \"%s\", \"overdue\": %s}", studentName, gson.toJson(overdue))));

                Upcoming[] upcoming = edioBridge.getUpcomingEvents(studentName);
                updateChannelState(CCAEdioBindingConstants.CHANNEL_UPCOMING, new StringType(
                        String.format("{ \"student\": \"%s\", \"upcoming\": %s}", studentName, gson.toJson(upcoming))));
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateChannels();
        } else {
            String channelId = channelUID.getId();
            for (ChannelHandler channelHandler : channelHandlers) {
                if (channelHandler.tryCommand(this, channelId, command)) {
                    return;
                }
            }
        }
    }

    @Override
    public void updateChannelState(String channelId, State state) {
        logger.trace("Updating {} to {}", channelId, state);
        updateState(channelId, state);
    }
}
