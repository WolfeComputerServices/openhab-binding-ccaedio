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

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.ccaedio.internal.CCAEdioBindingConstants;
import org.openhab.binding.ccaedio.internal.CCAEdioDiscovery;
import org.openhab.binding.ccaedio.internal.CCAEdioHandlerFactory;
import org.openhab.binding.ccaedio.internal.EdioAPIBridge;
import org.openhab.channhandler.ChannelHandler;
import org.openhab.channhandler.ChannelHandlerHasSchool;
import org.openhab.channhandler.ICCAThingHandler;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.wolfecomputerservices.edioapi.objects.Student;

/**
 * The {@link AccountBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public class AccountBridgeHandler extends BaseBridgeHandler implements ICCAThingHandler {
    private final Logger logger = LoggerFactory.getLogger(AccountBridgeHandler.class);
    private final AccountHandlerConfig config;
    private final EdioAPIBridge edioBridge;
    private final Gson gson;

    private static @Nullable ScheduledFuture<?> daily;

    private List<ChannelHandler> channelHandlers = new ArrayList<>();

    public AccountBridgeHandler(Bridge bridge, Gson gson) {
        super(bridge);

        // this.httpService = httpService;
        // this.stateStorage = stateStorage;
        this.gson = gson;
        config = getConfigAs(AccountHandlerConfig.class);
        edioBridge = new EdioAPIBridge(config, this.gson);
    }

    public EdioAPIBridge getEdio() {
        return edioBridge;
    }

    @Override
    public void dispose() {
        ScheduledFuture<?> localDaily = daily;
        if (localDaily != null) {
            if (!localDaily.isCancelled()) {
                localDaily.cancel(true);
                daily = null;
                logger.debug("Daily timer canceled and destroyed");
            }
        }
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(CCAEdioDiscovery.class);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information:
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");
        logger.trace("Command '{}' received for channel '{}'", command, channelUID);
        for (ChannelHandler handler : channelHandlers) {
            if (handler.tryCommand(this, channelUID.getId(), command)) {
                return;
            }
        }

        if (command instanceof RefreshType) {
            logger.trace("Refreshing data {}", getThing().getUID().getAsString());
            CCAEdioHandlerFactory.updateHandlers();
        }
    }

    private void updateChannels() {
        Boolean hasSchoolToday = edioBridge.hasSchoolToday();
        updateChannelState(CCAEdioBindingConstants.CHANNEL_HAS_SCHOOL, hasSchoolToday ? OnOffType.ON : OnOffType.OFF);
        logger.debug(String.format("Edio data: %s", hasSchoolToday.toString()));
    }

    @Override
    public void update() {
        logger.trace("Updating");
        updateChannels();
    }

    @Override
    public void initialize() {
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            ZonedDateTime nextRun = now.withHour(0).withMinute(30).withSecond(0);
            if (now.compareTo(nextRun) > 0) {
                nextRun = nextRun.plusDays(1);
            }

            Duration duration = Duration.between(now, nextRun);
            long initialDelay = duration.getSeconds();

            channelHandlers.add(new ChannelHandlerHasSchool(this, this.edioBridge, this.gson));
            ScheduledFuture<?> localDaily = daily;
            if (localDaily != null) {
                localDaily.cancel(true);
                daily = null;
                logger.debug("Daily timer canceled and destroyed");
            }
            daily = scheduler.scheduleWithFixedDelay(() -> {
                logger.debug("Running daily update");
                if (getThing().getStatus() == ThingStatus.ONLINE) {
                    CCAEdioHandlerFactory.updateHandlers();
                }
            }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
            logger.debug(String.format("Daily timer created: delayed %d hours", (initialDelay / 60) / 60));
            updateStatus(ThingStatus.ONLINE);
            updateChannels();
        });
    }

    @Override
    public void updateChannelState(String channelId, State state) {
        logger.debug("Updating {} to {}", channelId, state.toString());
        updateState(channelId, state);
    }

    public List<Student> updateStudentList() {
        return edioBridge.getStudents();
    }

    @Override
    public Object getConfigProperty(String propertyName) {
        return getConfig().get(propertyName);
    }
}
