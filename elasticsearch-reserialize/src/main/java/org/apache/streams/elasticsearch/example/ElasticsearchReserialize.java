package org.apache.streams.elasticsearch.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchReserialize {

    public final static String STREAMS_ID = "ElasticsearchReserialize";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchReserialize.class);

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

        Reserializer reserializer = new Reserializer();

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);
        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(1000), streamConfig);

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

            ObjectNode original;
            Activity originalActivity;
            String originalJson;

            String root = null;
            String extension = null;
            ObjectMapper mapper = null;
            ActivitySerializer serializer = null;

            try {
                original = objectMapper.convertValue(entry.getDocument(), ObjectNode.class);
                originalActivity = objectMapper.convertValue(entry.getDocument(), Activity.class);

                if( originalActivity.getId().contains("datasift")) {
                    extension = "datasift";
                    mapper = StreamsJacksonMapper.getInstance();
                    serializer = new DatasiftActivitySerializer();
                } else if( originalActivity.getProvider().getId().contains("twitter")) {
                    extension = "twitter";
                    mapper = StreamsTwitterMapper.getInstance();
                    serializer = new TwitterJsonActivitySerializer();
                }

                Preconditions.checkNotNull(mapper);
                Preconditions.checkNotNull(serializer);

                ObjectNode obj = null;

                if( root == null && extension == null ) {
                    obj = mapper.convertValue(original, ObjectNode.class);
                } else if( extension != null ) {
                    Map<String, Object> extensions = (Map<String, Object>) originalActivity.getAdditionalProperties().get("extensions");
                    obj = mapper.convertValue(extensions.get(extension), ObjectNode.class);
                } else if( root != null ) {
                    obj = mapper.convertValue(original.get(root), ObjectNode.class);
                }

                Preconditions.checkNotNull(obj);

                originalJson = objectMapper.writeValueAsString(obj);

                Activity resultDocument = null;

                String betterId = null;
                if( extension.equals("datasift")) {
                    Datasift ds = mapper.readValue(originalJson, Datasift.class);
                    resultDocument = serializer.deserialize(ds);
                    betterId = resultDocument.getId();
                } if( extension.equals("twitter")) {
                    resultDocument = serializer.deserialize(originalJson);
                    betterId = resultDocument.getId();
                } else {
                    resultDocument = serializer.deserialize(originalJson);
                }

                Preconditions.checkNotNull(resultDocument);

                StreamsDatum streamsDatum = new StreamsDatum(resultDocument, entry.getId(), entry.getTimestamp());

                if( betterId != null ) {
                    streamsDatum.setId(betterId);
                }

                resultList.add(streamsDatum);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ActivitySerializerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } catch(Error e){
                e.printStackTrace();
            } finally
            {
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
