package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.converter.ActivityConverterProcessor;
import org.apache.streams.converter.ActivityConverterProcessorConfiguration;
import org.apache.streams.converter.TypeConverterProcessor;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsPersistWriter;
import org.apache.streams.data.ActivityConverterResolver;
import org.apache.streams.data.DocumentClassifier;
import org.apache.streams.graph.GraphConfigurator;
import org.apache.streams.graph.GraphPersistWriter;
import org.apache.streams.graph.GraphWriterConfiguration;
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
public class TwitterUserstreamNeo4j {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterUserstreamNeo4j.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");
        Config graph = StreamsConfigurator.config.getConfig("graph");

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterConfigurator.detectTwitterStreamConfiguration(twitter);
        GraphWriterConfiguration graphWriterConfiguration = GraphConfigurator.detectWriterConfiguration(graph);

        StreamBuilder builder = new LocalStreamBuilder(100);

        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        TypeConverterProcessor converter = new TypeConverterProcessor(String.class);
        ActivityConverterProcessorConfiguration converterProcessorConfiguration = new ActivityConverterProcessorConfiguration()
                .withClassifiers(Lists.newArrayList((DocumentClassifier) TwitterDocumentClassifier.getInstance()))
                .withResolvers(Lists.newArrayList((ActivityConverterResolver) TwitterConverterResolver.getInstance()));

        ActivityConverterProcessor activity = new ActivityConverterProcessor(converterProcessorConfiguration);
        TypeConverterProcessor objectnode = new TypeConverterProcessor(ObjectNode.class);
        StreamsPersistWriter writer = (StreamsPersistWriter) new GraphPersistWriter(graphWriterConfiguration);

        builder.newPerpetualStream(TwitterStreamProvider.STREAMS_ID, stream);
        builder.addStreamsProcessor("converter", converter, 1, TwitterStreamProvider.STREAMS_ID);
        builder.addStreamsProcessor("activity", activity, 1, "converter");
        builder.addStreamsPersistWriter("console", new ConsolePersistWriter(), 1, "activity");
        builder.addStreamsProcessor("objectnode", objectnode, 1, "activity");
        builder.addStreamsPersistWriter(GraphPersistWriter.STREAMS_ID, writer, 1, "objectnode");
        builder.start();

    }
}
