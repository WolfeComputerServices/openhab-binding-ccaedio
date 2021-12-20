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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.ccaedio.internal.handler.AccountHandlerConfig;

import com.google.gson.Gson;
import com.wolfecomputerservices.edioapi.Edio;
import com.wolfecomputerservices.edioapi.objects.Configuration.Edio.Credentials;

/**
 * The {@link AccountBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
public final class EdioAPIBridge extends Edio {
    public EdioAPIBridge(AccountHandlerConfig config, Gson gson) {
        super(new Credentials(config.username, config.password), config.daysToRetrieve);
    }
}
