package org.apache.streams.datasift.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import io.dropwizard.Application;
import io.dropwizard.jackson.GuavaExtrasModule;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.datasift.processor.DatasiftTypeConverterProcessor;
import org.apache.streams.datasift.provider.DatasiftPushProvider;
import org.apache.streams.datasift.util.StreamsDatasiftMapper;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.converter.CleanAdditionalPropertiesProcessor;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamsApiApplication extends Application<StreamsApiConfiguration> {

    private static final Logger LOGGER = LoggerFactory
			.getLogger(StreamsApiApplication.class);

    private static ObjectMapper mapper = StreamsDatasiftMapper.getInstance();

    private StreamBuilder builder;

    private DatasiftPushProvider provider;
    private ElasticsearchPersistWriter writer;

    private Executor executor = Executors.newSingleThreadExecutor();

    private com.w2olabs.streams.DatasiftWebhookResource webhooks;


    static {
        mapper.registerModule(new AfterburnerModule());
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new GuavaExtrasModule());
    }

    @Override
    public void initialize(Bootstrap<StreamsApiConfiguration> bootstrap) {

        LOGGER.info(getClass().getPackage().getName());

    }

    @Override
    public void run(StreamsApiConfiguration streamsApiConfiguration, Environment environment) throws Exception {

        // streamsApiConfiguration = reconfigure(streamsApiConfiguration);
        provider = new com.w2olabs.streams.DatasiftWebhookResource();
        writer = new ElasticsearchPersistWriter(streamsApiConfiguration.getElasticsearch());

        executor = Executors.newSingleThreadExecutor();

        executor.execute(new StreamsLocalRunner());

        Thread.sleep(10000);

        environment.jersey().register(provider);

    }

    private class StreamsLocalRunner implements Runnable {

        @Override
        public void run() {

            Map<String, Object> streamConfig = Maps.newHashMap();
            streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000 * 1000);

            builder = new LocalStreamBuilder(1000, streamConfig);

            // prepare stream components
            builder.newPerpetualStream("webhooks", provider);
            builder.addStreamsProcessor("converter", new DatasiftTypeConverterProcessor(Activity.class), 2, "webhooks");
            builder.addStreamsProcessor("RemoveAdditionalProperties", new CleanAdditionalPropertiesProcessor(), 2, "converter");
            builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, writer, 1, "RemoveAdditionalProperties");

            builder.start();

        }
    }


    private StreamsApiConfiguration reconfigure(StreamsApiConfiguration streamsApiConfiguration) {

        // config from typesafe
        Config configTypesafe = StreamsConfigurator.config;

        // config from dropwizard
        Config configDropwizard = null;
        try {
            configDropwizard = ConfigFactory.parseString(mapper.writeValueAsString(streamsApiConfiguration));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error("Invalid Configuration: " + streamsApiConfiguration);
        }

        Config combinedConfig = configTypesafe.withFallback(configDropwizard);
        String combinedConfigJson = combinedConfig.root().render(ConfigRenderOptions.concise());

        StreamsApiConfiguration combinedDropwizardConfig = null;
        try {
            combinedDropwizardConfig = mapper.readValue(combinedConfigJson, StreamsApiConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Invalid Configuration after merge: " + streamsApiConfiguration);
        }

        return  combinedDropwizardConfig;

    }

    public static void main(String[] args) throws Exception
    {

        new StreamsApiApplication().run(args);

    }
}
