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

package fr.vsct.tock.bot.mongo

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.UpdateOptions
import fr.vsct.tock.bot.admin.bot.BotApplicationConfiguration
import fr.vsct.tock.bot.admin.bot.BotApplicationConfigurationDAO
import org.litote.kmongo.createIndex
import org.litote.kmongo.find
import org.litote.kmongo.getCollection
import org.litote.kmongo.json
import org.litote.kmongo.replaceOne

/**
 *
 */
object BotApplicationConfigurationMongoDAO : BotApplicationConfigurationDAO {

    private val col = MongoBotConfiguration.database.getCollection<BotApplicationConfiguration>("bot_configuration")

    init {
        col.createIndex("{applicationId:1, botId:1}", IndexOptions().unique(true))
    }

    override fun save(conf: BotApplicationConfiguration) {
        col.replaceOne("{applicationId:${conf.applicationId.json}, botId:${conf.botId.json}}", conf, UpdateOptions().upsert(true))
    }

    override fun findByNamespaceAndNlpModel(namespace: String, nlpModel: String): List<BotApplicationConfiguration> {
        return col.find("{namespace:${namespace.json}, nlpModel:${nlpModel.json}}").toList()
    }
}