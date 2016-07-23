/*
 * Copyright 2016 NinetySlide
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ninetyslide.libs.feta.util;

import com.ninetyslide.libs.feta.core.BotContext;
import com.ninetyslide.libs.feta.common.Constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton that manages all the BotContext available in the Bot. All the context are associated with two keys: the
 * pageId and the webhookUrl. All the interactions with them may be carried on with whatever key is preferred.
 */
public final class BotContextManager {

    private volatile static BotContextManager instance = null;

    private Map<String, BotContext> botContextByPageId;
    private Map<String, BotContext> botContextByWebhookUrl;

    private BotContextManager() {
        botContextByPageId = new ConcurrentHashMap<>();
        botContextByWebhookUrl = new ConcurrentHashMap<>();
    }

    public static BotContextManager getInstance() {
        if (instance == null) {
            synchronized (BotContextManager.class) {
                if (instance == null) {
                    instance = new BotContextManager();
                }
            }
        }
        return instance;
    }

    /**
     * Add the passed context to the context manager.
     *
     * @param newContext The context to add.
     */
    public synchronized void addContext(BotContext newContext) {
        if (newContext != null) {
            botContextByPageId.put(newContext.getPageId(), newContext);
            botContextByWebhookUrl.put(newContext.getWebhookUrl(), newContext);
        } else throw new IllegalArgumentException(Constants.MSG_CONTEXT_INVALID);
    }

    /**
     * Retrieve the context from the context manager using the passed key.
     *
     * @param contextKey The key of the desired context.
     * @return The context associated with the key, or null if there's no such context.
     */
    public BotContext getContext(String contextKey) {
        BotContext context = botContextByPageId.get(contextKey);
        if (context == null) {
            context = botContextByWebhookUrl.get(contextKey);
        }
        return context;
    }

    /**
     * Tells whether the context is present in the context manager using the passed key to search for it.
     *
     * @param contextKey The key of the desired context.
     * @return True if the context is present in the context manager, false otherwise.
     */
    public boolean containsContext(String contextKey) {
        return botContextByPageId.containsKey(contextKey) || botContextByWebhookUrl.containsKey(contextKey);
    }

    /**
     * Update the context specified by the key with the new instance passed as an argument.
     *
     * @param contextKey The key of the desired context.
     * @param updatedContext The new context to use for the specified key.
     */
    public synchronized void updateContext(String contextKey, BotContext updatedContext) {
        if (containsContext(contextKey)) {
            if (!contextKey.equals(updatedContext.getPageId()) && !contextKey.equals(updatedContext.getWebhookUrl())) {
                removeContext(contextKey);
            }
            botContextByPageId.put(updatedContext.getPageId(), updatedContext);
            botContextByWebhookUrl.put(updatedContext.getWebhookUrl(), updatedContext);
        }
    }

    /**
     * Remove a context from the context manager, using the passed key to search for it.
     *
     * @param contextKey The key of the desired context.
     */
    public synchronized void removeContext(String contextKey) {
        BotContext context = getContext(contextKey);

        if (context != null) {
            botContextByPageId.remove(context.getPageId());
            botContextByWebhookUrl.remove(context.getWebhookUrl());
        }
    }

}
