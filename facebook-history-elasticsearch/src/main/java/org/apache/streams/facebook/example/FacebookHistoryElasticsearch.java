package org.apache.streams.facebook.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.facebook.FacebookUserInformationConfiguration;
import org.apache.streams.facebook.provider.FacebookUserInformationProvider;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by sblackmon on 12/10/13.
 */
public class FacebookHistoryElasticsearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(FacebookHistoryElasticsearch.class);

    private static final ObjectMapper MAPPER = new StreamsJacksonMapper();

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config facebook = StreamsConfigurator.config.getConfig("facebook");
        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        FacebookUserInformationConfiguration facebookUserInformationConfiguration;
        try {
            facebookUserInformationConfiguration = MAPPER.readValue(facebook.root().render(ConfigRenderOptions.concise()), FacebookUserInformationConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        FacebookUserInformationProvider provider = new FacebookUserInformationProvider(facebookUserInformationConfiguration, String.class);

        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        ElasticsearchPersistWriter writer = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder();

        builder.newReadCurrentStream("provider", provider);
        builder.addStreamsPersistWriter("writer", writer, 1, "provider");
        builder.start();

    }

}
