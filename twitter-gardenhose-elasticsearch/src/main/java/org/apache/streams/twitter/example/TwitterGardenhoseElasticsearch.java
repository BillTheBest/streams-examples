package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.converter.ActivityConverterProcessor;
import org.apache.streams.converter.ActivityConverterProcessorConfiguration;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.data.ActivityConverterResolver;
import org.apache.streams.data.DocumentClassifier;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.apache.streams.twitter.serializer.TwitterConverterResolver;
import org.apache.streams.twitter.serializer.TwitterDocumentClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterGardenhoseElasticsearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterGardenhoseElasticsearch.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");
        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        StreamBuilder builder = new LocalStreamBuilder();

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterConfigurator.detectTwitterStreamConfiguration(twitter);
        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        ActivityConverterProcessor converter = new ActivityConverterProcessor(
                new ActivityConverterProcessorConfiguration()
                        .withClassifiers(Lists.newArrayList((DocumentClassifier) TwitterDocumentClassifier.getInstance()))
                        .withResolvers(Lists.newArrayList((ActivityConverterResolver) TwitterConverterResolver.getInstance()))
        );

        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        ElasticsearchPersistWriter elasticsearchWriter = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        builder.newPerpetualStream("provider", stream);
        builder.addStreamsProcessor("converter", converter, 4, "provider");
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchWriter, 1, "converter");

        builder.start();
    }
}
