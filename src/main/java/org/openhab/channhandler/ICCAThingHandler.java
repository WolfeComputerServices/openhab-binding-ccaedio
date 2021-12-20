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
import org.openhab.core.types.State;

/**
 * The {@link AccountBridgeHandler} interface for ChannelHandler=>thing communication.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public interface ICCAThingHandler {
    public abstract void update();

    void updateChannelState(String channelId, State state);

    Object getConfigProperty(String propertyName);
}
