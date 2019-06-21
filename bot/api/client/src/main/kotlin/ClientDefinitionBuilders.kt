/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.vsct.tock.bot.api.client

import fr.vsct.tock.bot.definition.Intent

private fun defaultUnknownStory() = unknownStory { end("Sorry I didn't understand") }

/**
 * Create a story addressing [Intent.unknown] intent.
 */
fun unknownStory(
    /**
     * The handler for the story.
     */
    handler: (ClientBus).() -> Unit) = ClientStoryDefinition(Intent.unknown, newStoryHandler(handler))

/**
 * Creates a new bot.
 */
fun newBot(
    apiKey: String,
    /**
     * List of stories supported by the bot.
     */
    stories: List<ClientStoryDefinition>,
    unknownStory: ClientStoryDefinition = defaultUnknownStory()
): ClientBotDefinition = ClientBotDefinition(apiKey, stories, unknownStory)

/**
 * Creates a new bot.
 */
fun newBot(
    apiKey: String,
    /**
     * List of stories supported by the bot.
     */
    vararg stories: ClientStoryDefinition
): ClientBotDefinition =
    newBot(
        apiKey,
        stories.toList(),
        stories.find { it.wrap(Intent.unknown) } ?: defaultUnknownStory()
    )

/**
 * Creates a new story.
 */
fun newStory(
    /**
     * The main intent.
     */
    mainIntent: String,
    /**
     * The handler for the story.
     */
    handler: (ClientBus).() -> Unit
): ClientStoryDefinition =
    ClientStoryDefinition(
        Intent(mainIntent),
        newStoryHandler(handler)
    )

fun newStoryHandler(handler: (ClientBus).() -> Unit): ClientStoryHandler =
    object : ClientStoryHandler {
        override fun handle(bus: ClientBus) {
            handler(bus)
        }
    }