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
import org.openhab.binding.ccaedio.internal.CCAEdioBindingConstants;
import org.openhab.binding.ccaedio.internal.EdioAPIBridge;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;

import com.google.gson.Gson;

/**
 * The {@link ChannelHandlerHasSchool} is responsible for the has school updating.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public class ChannelHandlerHasSchool extends ChannelHandler {
    private static final String CHANNEL_NAME = CCAEdioBindingConstants.CHANNEL_HAS_SCHOOL;

    // private final Logger logger = Logger.getLogger(ChannelHandlerHasSchool.class);

    public ChannelHandlerHasSchool(ICCAThingHandler thingHandler, EdioAPIBridge edioBridge, Gson gson) {
        super(thingHandler, edioBridge, gson);
    }

    @Override
    public boolean tryCommand(ThingHandler parentHandler, String channelId, Command command) {
        if (CHANNEL_NAME.equals(channelId)) {
            if (command instanceof OnOffType && command == OnOffType.ON) {
                String studentName = (String) thingHandler
                        .getConfigProperty(CCAEdioBindingConstants.STUDENT_PROPERTY_NAME);
                thingHandler.updateChannelState(CHANNEL_NAME,
                        edioBridge.hasOverdues(studentName) ? OnOffType.ON : OnOffType.OFF);
            }
            return true;
        }

        return false;
    }
}
