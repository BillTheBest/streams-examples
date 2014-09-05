package org.apache.streams.elasticsearch.example;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistReader;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.elasticsearch.processor.MetadataAsDocumentProcessor;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchDelete implements Runnable {

    public final static String STREAMS_ID = "ElasticsearchDelete";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDelete.class);

    public static void main(String[] args)
    {
        ElasticsearchDelete job = new ElasticsearchDelete();
        (new Thread(job)).start();

    }

    @Override
    public void run() {

        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        ElasticsearchWriterConfiguration elasticsearchConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        ElasticsearchPersistDeleter elasticsearchPersistDeleter = new ElasticsearchPersistDeleter(elasticsearchConfiguration);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);
        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(1000), streamConfig);

        builder.newReadCurrentStream("console", new ConsolePersistReader());
        builder.addStreamsProcessor(MetadataAsDocumentProcessor.STREAMS_ID, new MetadataAsDocumentProcessor(), 1, "console");
        builder.addStreamsPersistWriter(ElasticsearchPersistDeleter.STREAMS_ID, elasticsearchPersistDeleter, 1, MetadataAsDocumentProcessor.STREAMS_ID);
        builder.start();

    }



}
