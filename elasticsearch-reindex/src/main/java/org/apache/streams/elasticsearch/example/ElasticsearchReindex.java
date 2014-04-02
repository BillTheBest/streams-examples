package org.apache.streams.elasticsearch.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchReindex {

    public final static String STREAMS_ID = "ElasticsearchReindex";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchReindex.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    protected ListeningExecutorService executor = MoreExecutors.listeningDecorator(newFixedThreadPoolWithQueueSize(5, 20));

    private static ExecutorService newFixedThreadPoolWithQueueSize(int nThreads, int queueSize) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config reindex = StreamsConfigurator.config.getConfig("reindex");

        Config source = reindex.getConfig("source");
        Config destination = reindex.getConfig("destination");

        ElasticsearchConfiguration elasticsearchSourceConfiguration = ElasticsearchConfigurator.detectConfiguration(source);
        ElasticsearchConfiguration elasticsearchDestinationConfiguration = ElasticsearchConfigurator.detectConfiguration(destination);

        ElasticsearchReaderConfiguration elasticsearchReaderConfiguration  = mapper.convertValue(elasticsearchSourceConfiguration, ElasticsearchReaderConfiguration.class);
        ElasticsearchPersistReader elasticsearchPersistReader = new ElasticsearchPersistReader(elasticsearchReaderConfiguration);

        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration  = mapper.convertValue(elasticsearchDestinationConfiguration, ElasticsearchWriterConfiguration.class);

        ElasticsearchPersistWriter elasticsearchPersistWriter = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(1000));

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchPersistReader);
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchPersistWriter, 1, ElasticsearchPersistReader.STREAMS_ID);
        builder.start();

    }

}
