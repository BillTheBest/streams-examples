/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.streams.facebook.example;

import org.apache.streams.facebook.processor.FacebookTypeConverter;
import org.apache.streams.facebook.provider.FacebookFriendUpdatesProvider;
import org.apache.streams.facebook.provider.FacebookUserstreamProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.facebook.FacebookUserstreamConfiguration;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class FacebookFriendUpdatesElasticsearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(FacebookFriendUpdatesElasticsearch.class);

    private static final ObjectMapper MAPPER = new StreamsJacksonMapper();

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config facebook = StreamsConfigurator.config.getConfig("facebook");
        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        FacebookUserstreamConfiguration facebookUserstreamConfiguration;
        try {
            facebookUserstreamConfiguration = MAPPER.readValue(facebook.root().render(ConfigRenderOptions.concise()), FacebookUserstreamConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        FacebookFriendUpdatesProvider provider = new FacebookFriendUpdatesProvider(facebookUserstreamConfiguration, ObjectNode.class);
        FacebookTypeConverter converter = new FacebookTypeConverter(ObjectNode.class, Activity.class);
        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);
        ElasticsearchPersistWriter writer = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder();

        builder.newPerpetualStream(FacebookUserstreamProvider.STREAMS_ID, provider);
        builder.addStreamsProcessor("converter", converter, 2, FacebookUserstreamProvider.STREAMS_ID);
        builder.addStreamsPersistWriter("console", writer, 1, "converter");
        builder.start();

    }

}
