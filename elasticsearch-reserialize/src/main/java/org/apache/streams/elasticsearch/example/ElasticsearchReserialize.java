package org.apache.streams.elasticsearch.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsProcessor;
import org.apache.streams.data.ActivitySerializer;
import org.apache.streams.datasift.Datasift;
import org.apache.streams.datasift.serializer.DatasiftActivitySerializer;
import org.apache.streams.datasift.twitter.Twitter;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.exceptions.ActivitySerializerException;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.serializer.StreamsTwitterMapper;
import org.apache.streams.twitter.serializer.TwitterJsonActivitySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchReserialize {

    public final static String STREAMS_ID = "ElasticsearchReserialize";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchReserialize.class);

    private final static ObjectMapper streamsJacksonMapper = StreamsJacksonMapper.getInstance();

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

        Reserializer reserializer = new Reserializer();

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(1000));

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchPersistReader);
        builder.addStreamsProcessor(Reserializer.STREAMS_ID, reserializer, 2, ElasticsearchPersistReader.STREAMS_ID);
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchPersistWriter, 1, Reserializer.STREAMS_ID);
        builder.start();

    }

    public static class Reserializer implements StreamsProcessor {

        public Reserializer() {
        }

        public final static String STREAMS_ID = "Reserializer";

        @Override
        public List<StreamsDatum> process(StreamsDatum entry) {

            List<StreamsDatum> resultList = Lists.newArrayList();

            Activity originalActivity;
            String originalJson;

            String channel = null;
            ObjectMapper mapper = null;
            ActivitySerializer serializer = null;

            try {
                originalActivity = streamsJacksonMapper.convertValue(entry.getDocument(), Activity.class);

                if( originalActivity.getProvider().getId().contains("twitter")) {
                    channel = "twitter";
                    mapper = StreamsTwitterMapper.getInstance();
                    serializer = new TwitterJsonActivitySerializer();
                } else if( originalActivity.getProvider().getId().contains("datasift")) {
                    channel = "datasift";
                    mapper = streamsJacksonMapper;
                    serializer = new DatasiftActivitySerializer();
                }

                Preconditions.checkNotNull(channel);
                Preconditions.checkNotNull(mapper);
                Preconditions.checkNotNull(serializer);

                originalJson = mapper.writeValueAsString(originalActivity.getAdditionalProperties().get(channel));

                Activity resultDocument = null;

                resultDocument = serializer.deserialize(originalJson);

                Preconditions.checkNotNull(resultDocument);

                StreamsDatum streamsDatum = new StreamsDatum(resultDocument, entry.getId(), entry.getTimestamp());

                resultList.add(streamsDatum);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ActivitySerializerException e) {
                e.printStackTrace();
            } finally {
                return resultList;
            }

        }

        @Override
        public void prepare(Object configurationObject) {

        }

        @Override
        public void cleanUp() {

        }
    }

}
