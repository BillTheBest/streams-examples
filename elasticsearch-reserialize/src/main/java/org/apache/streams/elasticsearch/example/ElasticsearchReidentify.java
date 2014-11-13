package org.apache.streams.elasticsearch.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsProcessor;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.jackson.CleanAdditionalPropertiesProcessor;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by steveblackmon on 9/24/14.
 */
public class ElasticsearchReidentify {

    public final static String STREAMS_ID = "ElasticsearchReidentify";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchReidentify.class);

    private final static ObjectMapper objectMapper = StreamsJacksonMapper.getInstance();

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

        ElasticsearchReaderConfiguration elasticsearchSourceConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(source);

        ElasticsearchPersistReader elasticsearchPersistReader = new ElasticsearchPersistReader(elasticsearchSourceConfiguration);

        ElasticsearchWriterConfiguration elasticsearchDestinationConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(destination);

        ElasticsearchPersistWriter elasticsearchPersistWriter = new ElasticsearchPersistWriter(elasticsearchDestinationConfiguration);

        CleanAdditionalPropertiesProcessor cleanAdditionalPropertiesProcessor = new CleanAdditionalPropertiesProcessor();

        Reidentifier reidentifier = new Reidentifier();

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);
        StreamBuilder builder = new LocalStreamBuilder(1000, streamConfig);

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchPersistReader);
        builder.addStreamsProcessor("CleanAdditionalPropertiesProcessor", cleanAdditionalPropertiesProcessor, 2, ElasticsearchPersistReader.STREAMS_ID);
        builder.addStreamsProcessor(Reidentifier.STREAMS_ID, reidentifier, 2, "CleanAdditionalPropertiesProcessor");
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchPersistWriter, 1, Reidentifier.STREAMS_ID);
        builder.start();

    }

    public static class Reidentifier implements StreamsProcessor {

        public Reidentifier() {
        }

        ObjectMapper mapper = null;

        public final static String STREAMS_ID = "Reidentifier";

        @Override
        public List<StreamsDatum> process(StreamsDatum entry) {

            List<StreamsDatum> resultList = Lists.newArrayList();

            ObjectNode document;

            try {
                document = mapper.convertValue(entry.getDocument(), ObjectNode.class);

                ObjectNode extensions = (ObjectNode) document.get("extensions");
                ObjectNode datasift = (ObjectNode) extensions.get("datasift");
                ObjectNode twitter = (ObjectNode) datasift.get("twitter");
                String twitterId = twitter.get("id").asText();
                Collection<String> parts = Lists.newArrayList("id","twitter","post",twitterId);
                String streamsId = Joiner.on(":").join(parts);

                document.put("id", streamsId);

                StreamsDatum streamsDatum = new StreamsDatum(document, streamsId, entry.getTimestamp());

                resultList.add(streamsDatum);

            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            } finally {
                return resultList;
            }
        }

        @Override
        public void prepare(Object configurationObject) {
            mapper = StreamsJacksonMapper.getInstance();
        }

        @Override
        public void cleanUp() {

        }
    }
}
