package org.apache.streams.elasticsearch.example;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistReader;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchReaderConfiguration;
import org.apache.streams.elasticsearch.processor.DatumFromMetadataAsDocumentProcessor;
import org.apache.streams.elasticsearch.processor.DatumFromMetadataProcessor;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchProviderFromMetadataFile implements Runnable {

    public final static String STREAMS_ID = "ElasticsearchJoin";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchProviderFromMetadataFile.class);

    public static void main(String[] args)
    {
        ElasticsearchProviderFromMetadataFile job = new ElasticsearchProviderFromMetadataFile();
        (new Thread(job)).start();

    }

    @Override
    public void run() {

        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        ElasticsearchReaderConfiguration elasticsearchConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(elasticsearch);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);
        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(1000), streamConfig);

        builder.newPerpetualStream("consolein", new ConsolePersistReader());
        builder.addStreamsProcessor(DatumFromMetadataAsDocumentProcessor.STREAMS_ID, new DatumFromMetadataAsDocumentProcessor(), 1, "consolein");
        builder.addStreamsProcessor(DatumFromMetadataProcessor.STREAMS_ID, new DatumFromMetadataProcessor(elasticsearchConfiguration), 1, DatumFromMetadataAsDocumentProcessor.STREAMS_ID);
        builder.addStreamsPersistWriter("consoleout", new ConsolePersistWriter(), 1, DatumFromMetadataProcessor.STREAMS_ID);
        builder.start();

    }

}
