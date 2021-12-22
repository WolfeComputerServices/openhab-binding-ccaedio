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

import static org.openhab.binding.ccaedio.internal.CCAEdioBindingConstants.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.ccaedio.internal.handler.AccountBridgeHandler;
import org.openhab.binding.ccaedio.internal.handler.StudentHandler;
import org.openhab.channhandler.ICCAThingHandler;
import org.openhab.core.storage.StorageService;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link CCAEdioHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Wolfe Computer Services - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.ccaedio", service = ThingHandlerFactory.class)
public class CCAEdioHandlerFactory extends BaseThingHandlerFactory {

    private static final Logger logger = LoggerFactory.getLogger(CCAEdioHandlerFactory.class);
    private final Set<AccountBridgeHandler> accountHandlers = new HashSet<>();
    private static final Gson gson = new Gson();

    private static final Set<ICCAThingHandler> handlers = new HashSet<>();

    @Activate
    public CCAEdioHandlerFactory(@Reference HttpService httpService, @Reference StorageService storageService) {
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    public void deactivate(ComponentContext componentContext) {
        super.deactivate(componentContext);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_ACCOUNT.equals(thingTypeUID)) {
            AccountBridgeHandler accoundHandler = new AccountBridgeHandler((Bridge) thing, gson);
            accountHandlers.add(accoundHandler);
            registerCCAEdioThingHandler(accoundHandler);
            return accoundHandler;
        } else if (THING_TYPE_STUDENT.equals(thingTypeUID)) {
            return registerCCAEdioThingHandler(new StudentHandler(thing, gson));
        }

        return null;
    }

    private ThingHandler registerCCAEdioThingHandler(ICCAThingHandler newHandler) {
        handlers.add(newHandler);
        return (ThingHandler) newHandler;
    }

    public static void updateHandlers() {
        handlers.stream().forEach(h -> {
            if (((ThingHandler) h).getThing().getStatus() == ThingStatus.ONLINE) {
                logger.debug(String.format("Running update on %s", ((ThingHandler) h).getThing().getUID().toString()));
                h.update();
            }
        });
    }

    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof AccountBridgeHandler) {
            accountHandlers.remove(thingHandler);
        } else if (thingHandler instanceof ICCAThingHandler) {
            handlers.remove((ICCAThingHandler) thingHandler);
        }
    }
}
