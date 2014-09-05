package org.apache.streams.elasticsearch.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistReader;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchQuery implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchQuery.class);

    public final static String STREAMS_ID = "ElasticsearchQuery";

    private final static ObjectMapper mapper = new ObjectMapper();

    protected ListeningExecutorService executor = MoreExecutors.listeningDecorator(newFixedThreadPoolWithQueueSize(5, 20));

    private static ExecutorService newFixedThreadPoolWithQueueSize(int nThreads, int queueSize) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void main(String[] args)
    {
        ElasticsearchQuery job = new ElasticsearchQuery();
        (new Thread(job)).start();

    }


    @Override
    public void run() {

        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        ElasticsearchReaderConfiguration elasticsearchReaderConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(elasticsearch);

        ElasticsearchPersistReader elasticsearchReader = new ElasticsearchPersistReader(elasticsearchReaderConfiguration);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);
        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(1000), streamConfig);

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchReader);
        builder.addStreamsPersistWriter("console", new ConsoleMetadataWriter(), 1, ElasticsearchPersistReader.STREAMS_ID);
        builder.start();

    }

    public class ConsoleMetadataWriter extends ConsolePersistWriter {

        @Override
        public void write(StreamsDatum entry) {

            try {

                String text = mapper.writeValueAsString(entry.getMetadata());

                System.out.println(text);
//            LOGGER.info(text);

            } catch (JsonProcessingException e) {
                LOGGER.warn("save: {}", e);
            }

        }
    }

}
