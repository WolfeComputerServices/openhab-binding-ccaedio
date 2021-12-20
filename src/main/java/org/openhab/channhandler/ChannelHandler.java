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
package org.openhab.channhandler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.ccaedio.internal.EdioAPIBridge;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link AccountBridgeHandler} base class for handlers.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public abstract class ChannelHandler {
    public abstract boolean tryCommand(ThingHandler parentHandler, String channelId, Command command);

    protected final ICCAThingHandler thingHandler;
    protected final Logger logger;
    protected final EdioAPIBridge edioBridge;
    protected final Gson gson;

    protected ChannelHandler(ICCAThingHandler thingHandler, EdioAPIBridge edioBridge, Gson gson) {
        this.logger = LoggerFactory.getLogger(this.getClass());

        this.edioBridge = edioBridge;
        this.thingHandler = thingHandler;
        this.gson = gson;
    }
}
