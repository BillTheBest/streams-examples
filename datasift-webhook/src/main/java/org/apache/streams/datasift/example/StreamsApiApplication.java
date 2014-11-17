package org.apache.streams.datasift.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.collect.Maps;
import io.dropwizard.Application;
import io.dropwizard.jackson.GuavaExtrasModule;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.datasift.Datasift;
import org.apache.streams.datasift.processor.DatasiftActivitySerializerProcessor;
import org.apache.streams.datasift.provider.DatasiftPushProvider;
import org.apache.streams.datasift.util.StreamsDatasiftMapper;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.hdfs.WebHdfsPersistWriter;
import org.apache.streams.converter.TypeConverterProcessor;
import org.apache.streams.kafka.KafkaPersistWriter;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.s3.S3PersistWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StreamsApiApplication extends Application<StreamsApiConfiguration> {

    private static final Logger LOGGER = LoggerFactory
			.getLogger(StreamsApiApplication.class);

    private static ObjectMapper mapper = StreamsDatasiftMapper.getInstance();

    private StreamBuilder builder;

    private StreamsApiConfiguration configuration;

    private DatasiftPushProvider provider;

    private Executor executor = Executors.newSingleThreadExecutor();

    private DatasiftWebhookResource webhook;

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
    public void run(StreamsApiConfiguration configuration, Environment environment) throws Exception {

        this.configuration = configuration;

        provider = new DatasiftWebhookResource();

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
            builder.newPerpetualStream("webhook", provider);
            builder.addStreamsProcessor("converter", new TypeConverterProcessor(String.class, Datasift.class), 2, "webhook");
            builder.addStreamsProcessor("serializer", new DatasiftActivitySerializerProcessor(Activity.class), 2, "converter");

            if( configuration.getElasticsearch() != null )
                builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, new ElasticsearchPersistWriter(configuration.getElasticsearch()), 1, "serializer");

            if( configuration.getHdfs() != null )
                builder.addStreamsPersistWriter(WebHdfsPersistWriter.STREAMS_ID, new WebHdfsPersistWriter(configuration.getHdfs()), 1, "serializer");

            if( configuration.getKafka() != null )
                builder.addStreamsPersistWriter("kafka", new KafkaPersistWriter(), 1, "serializer");

            if( configuration.getS3() != null )
                builder.addStreamsPersistWriter(S3PersistWriter.STREAMS_ID, new S3PersistWriter(configuration.getS3()), 1, "serializer");

            builder.start();

        }
    }

    public static void main(String[] args) throws Exception
    {

        new StreamsApiApplication().run(args);

    }
}
