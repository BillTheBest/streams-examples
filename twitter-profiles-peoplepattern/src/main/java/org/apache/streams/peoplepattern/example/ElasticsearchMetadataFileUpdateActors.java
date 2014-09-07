package org.apache.streams.peoplepattern.example;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistReader;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.elasticsearch.processor.DatumFromMetadataProcessor;
import org.apache.streams.elasticsearch.processor.MetadataAsDocumentProcessor;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.peoplepattern.AccountTypeProcessor;
import org.apache.streams.peoplepattern.DemographicsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchMetadataFileUpdateActors implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchMetadataFileUpdateActors.class);

    public static void main(String[] args)
    {
        ElasticsearchMetadataFileUpdateActors job = new ElasticsearchMetadataFileUpdateActors();
        (new Thread(job)).start();

    }

    @Override
    public void run() {

        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        ElasticsearchConfiguration elasticsearchConfiguration = ElasticsearchConfigurator.detectConfiguration(elasticsearch);
        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);
        ElasticsearchReaderConfiguration elasticsearchReaderConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(elasticsearch);

        ElasticsearchPersistUpdater elasticsearchPersistUpdater = new ElasticsearchPersistUpdater(elasticsearchWriterConfiguration);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(10), streamConfig);

        AccountTypeProcessor accountTypeProcessor = new AccountTypeProcessor();
        DemographicsProcessor demographicsProcessor = new DemographicsProcessor();

        builder.newReadCurrentStream("console", new ConsolePersistReader());
        builder.addStreamsProcessor(MetadataAsDocumentProcessor.STREAMS_ID, new MetadataAsDocumentProcessor(), 1, "console");
        builder.addStreamsProcessor(DatumFromMetadataProcessor.STREAMS_ID, new DatumFromMetadataProcessor(elasticsearchReaderConfiguration), 1, MetadataAsDocumentProcessor.STREAMS_ID);
        builder.addStreamsProcessor("accountTypeProcessor", accountTypeProcessor, 3, DatumFromMetadataProcessor.STREAMS_ID);
        builder.addStreamsProcessor("demographicsProcessor", demographicsProcessor, 3, "accountTypeProcessor");

        builder.addStreamsPersistWriter("updater", elasticsearchPersistUpdater, 1, "demographicsProcessor");

        builder.start();
    }
}
