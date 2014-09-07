package org.apache.streams.peoplepattern.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsProcessor;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.elasticsearch.processor.DatumFromMetadataAsDocumentProcessor;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.pojo.json.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchQueryActivityUpdateActorsFromDirectory implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchQueryActivityUpdateActorsFromDirectory.class);

    public static void main(String[] args)
    {
        ElasticsearchQueryActivityUpdateActorsFromDirectory job = new ElasticsearchQueryActivityUpdateActorsFromDirectory();
        (new Thread(job)).start();

    }

    @Override
    public void run() {

        LOGGER.info(StreamsConfigurator.config.toString());

        Config enhance = StreamsConfigurator.config.getConfig("enhance");

        ElasticsearchReaderConfiguration elasticsearchSourceConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(enhance.getConfig("source"));

        ElasticsearchPersistReader elasticsearchPersistReader = new ElasticsearchPersistReader(elasticsearchSourceConfiguration);

        ElasticsearchReaderConfiguration elasticsearchDirectoryConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(enhance.getConfig("directory"));

        DirectoryMetadataProcessor directoryMetadataProcessor = new DirectoryMetadataProcessor();

        DatumFromMetadataAsDocumentProcessor elasticsearchDirectoryReader = new DatumFromMetadataAsDocumentProcessor(elasticsearchDirectoryConfiguration);

        ElasticsearchWriterConfiguration elasticsearchDestinationConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(enhance.getConfig("destination"));

        ElasticsearchPersistUpdater elasticsearchPersistUpdater = new ElasticsearchPersistUpdater(elasticsearchDestinationConfiguration);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(10), streamConfig);

        builder.newPerpetualStream("provider", elasticsearchPersistReader);
        builder.addStreamsProcessor("actorMetadata", directoryMetadataProcessor, 1, "provider");
        builder.addStreamsProcessor("lookup", elasticsearchDirectoryReader, 1, "actorMetadata");
        builder.addStreamsProcessor("actorOnly", new PeoplePatternActorExtensionsOnlyProcessor(), 1, "lookup");
        builder.addStreamsPersistWriter("updater", elasticsearchPersistUpdater, 1, "actorOnly");

        builder.start();
    }

    public class DirectoryMetadataProcessor implements StreamsProcessor, Serializable {

        private ObjectMapper mapper;

        @Override
        public List<StreamsDatum> process(StreamsDatum entry) {
            List<StreamsDatum> result = Lists.newArrayList();

            if(entry == null || entry.getMetadata() == null)
                return result;

            Activity activity = mapper.convertValue(entry.getDocument(), Activity.class);
            Actor actor = activity.getActor();
            String actorId = actor.getId();

            Map<String, Object> actorMetadata = Maps.newHashMap();

            actorMetadata.put("id", actorId);

            StreamsDatum actorMetadataDatum = new StreamsDatum(entry.getDocument());
            actorMetadataDatum.setMetadata(entry.getMetadata());

            String actorMetadataAsDocument = null;
            try {
                actorMetadataAsDocument = mapper.writeValueAsString(actorMetadata);
            } catch (JsonProcessingException e) {
                return result;
            }
            actorMetadataDatum.setDocument(actorMetadataAsDocument);

            result.add(actorMetadataDatum);

            return result;

        }

        @Override
        public void prepare(Object configurationObject) {
            mapper = StreamsJacksonMapper.getInstance();
            mapper.registerModule(new JsonOrgModule());

        }

        @Override
        public void cleanUp() {

        }
    }

    public class PeoplePatternActorExtensionsOnlyProcessor implements StreamsProcessor, Serializable {

        private ObjectMapper mapper;

        @Override
        public List<StreamsDatum> process(StreamsDatum entry) {
            List<StreamsDatum> result = Lists.newArrayList();

            if(entry == null || entry.getMetadata() == null)
                return result;

            Activity activity = mapper.convertValue(entry.getDocument(), Activity.class);
            Actor actor = activity.getActor();

            Activity actorOnlyActivity = new Activity();
            actorOnlyActivity.setActor(actor);

            StreamsDatum actorOnlyDatum = new StreamsDatum(actorOnlyActivity);
            actorOnlyDatum.setMetadata(entry.getMetadata());

            result.add(actorOnlyDatum);

            return result;

        }

        @Override
        public void prepare(Object configurationObject) {
            mapper = StreamsJacksonMapper.getInstance();
            mapper.registerModule(new JsonOrgModule());

        }

        @Override
        public void cleanUp() {

        }
    }
}
