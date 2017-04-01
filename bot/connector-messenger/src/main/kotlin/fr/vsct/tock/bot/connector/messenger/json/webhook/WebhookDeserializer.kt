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

package fr.vsct.tock.bot.connector.messenger.json.webhook

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import fr.vsct.tock.bot.connector.messenger.model.Recipient
import fr.vsct.tock.bot.connector.messenger.model.Sender
import fr.vsct.tock.bot.connector.messenger.model.webhook.Message
import fr.vsct.tock.bot.connector.messenger.model.webhook.MessageEcho
import fr.vsct.tock.bot.connector.messenger.model.webhook.MessageEchoWebhook
import fr.vsct.tock.bot.connector.messenger.model.webhook.MessageWebhook
import fr.vsct.tock.bot.connector.messenger.model.webhook.Optin
import fr.vsct.tock.bot.connector.messenger.model.webhook.OptinWebhook
import fr.vsct.tock.bot.connector.messenger.model.webhook.Postback
import fr.vsct.tock.bot.connector.messenger.model.webhook.PostbackWebhook
import fr.vsct.tock.bot.connector.messenger.model.webhook.Webhook
import fr.vsct.tock.shared.jackson.readValueAs
import mu.KotlinLogging

/**
 *
 */
class WebhookDeserializer : JsonDeserializer<Webhook>() {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Webhook? {
        var sender: Sender? = null
        var recipient: Recipient? = null
        var timestamp: Long? = null
        var message: Message? = null
        var optin: Optin? = null
        var postback: Postback? = null
        while (jp.nextValue() != JsonToken.END_OBJECT) {
            when (jp.currentName) {
                Webhook::sender.name -> sender = jp.readValueAs(Sender::class)
                Webhook::recipient.name -> recipient = jp.readValueAs(Recipient::class)
                Webhook::timestamp.name -> timestamp = jp.longValue
                MessageWebhook::message.name -> message = jp.readValueAs(Message::class)
                OptinWebhook::optin.name -> optin = jp.readValueAs(Optin::class)
                PostbackWebhook::postback.name -> postback = jp.readValueAs(Postback::class)
                else -> logger.warn { "Unsupported field : ${jp.currentName}" }
            }
        }

        if (sender == null || recipient == null || timestamp == null) {
            logger.warn { "invalid webhook $sender $recipient $timestamp" }
            return null
        }

        return if (message != null) {
            when (message) {
                is MessageEcho -> MessageEchoWebhook(sender, recipient, timestamp, message)
                else -> MessageWebhook(sender, recipient, timestamp, message)
            }

        } else if (optin != null) {
            OptinWebhook(sender, recipient, timestamp, optin)
        } else if (postback != null) {
            PostbackWebhook(sender, recipient, timestamp, postback)
        } else {
            logger.warn { "unknown webhook" }
            null
        }
    }
}