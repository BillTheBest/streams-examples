package org.apache.streams.facebook.example;

import org.apache.streams.facebook.processor.FacebookTypeConverter;
import org.apache.streams.facebook.provider.FacebookFriendFeedProvider;
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
public class FacebookFriendFeedElasticsearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(FacebookFriendFeedElasticsearch.class);

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

        FacebookFriendFeedProvider provider = new FacebookFriendFeedProvider(facebookUserstreamConfiguration, ObjectNode.class);
        FacebookTypeConverter converter = new FacebookTypeConverter(ObjectNode.class, Activity.class);
        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);
        ElasticsearchPersistWriter writer = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder();

        builder.newPerpetualStream(FacebookFriendFeedProvider.STREAMS_ID, provider);
        builder.addStreamsProcessor("converter", converter, 2, FacebookFriendFeedProvider.STREAMS_ID);
        builder.addStreamsPersistWriter("console", writer, 1, "converter");
        builder.start();

    }

}
