package org.apache.streams.peoplepattern.example;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistReader;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.*;
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
public class ElasticsearchQueryDirectoryUpdateActors implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchQueryDirectoryUpdateActors.class);

    public static void main(String[] args)
    {
        ElasticsearchQueryDirectoryUpdateActors job = new ElasticsearchQueryDirectoryUpdateActors();
        (new Thread(job)).start();

    }

    @Override
    public void run() {

        LOGGER.info(StreamsConfigurator.config.toString());

        Config enhance = StreamsConfigurator.config.getConfig("enhance");

        ElasticsearchReaderConfiguration elasticsearchSourceConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(enhance.getConfig("source"));

        ElasticsearchPersistReader elasticsearchPersistReader = new ElasticsearchPersistReader(elasticsearchSourceConfiguration);

        ElasticsearchWriterConfiguration elasticsearchDestinationConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(enhance.getConfig("destination"));

        ElasticsearchPersistUpdater elasticsearchPersistUpdater = new ElasticsearchPersistUpdater(elasticsearchDestinationConfiguration);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(10), streamConfig);

        AccountTypeProcessor accountTypeProcessor = new AccountTypeProcessor();
        DemographicsProcessor demographicsProcessor = new DemographicsProcessor();

        builder.newPerpetualStream("console", new ConsolePersistReader());
        builder.addStreamsProcessor(MetadataAsDocumentProcessor.STREAMS_ID, new MetadataAsDocumentProcessor(), 1, "console");
        builder.addStreamsProcessor("accountTypeProcessor", accountTypeProcessor, 3, "provider");
        builder.addStreamsProcessor("demographicsProcessor", demographicsProcessor, 3, "accountTypeProcessor");

        builder.addStreamsPersistWriter("updater", elasticsearchPersistUpdater, 1, "demographicsProcessor");

        builder.start();
    }
}
