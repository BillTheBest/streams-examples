package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.processor.TwitterTypeConverter;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterUserstreamConsole {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterUserstreamConsole.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterConfigurator.detectTwitterStreamConfiguration(twitter);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(100));

        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        TwitterTypeConverter converter = new TwitterTypeConverter(ObjectNode.class, String.class);
        ConsolePersistWriter console = new ConsolePersistWriter();

        builder.newPerpetualStream(TwitterStreamProvider.STREAMS_ID, stream);
        builder.addStreamsProcessor("converter", converter, 2, TwitterStreamProvider.STREAMS_ID);
        builder.addStreamsPersistWriter("console", console, 1, "converter");
        builder.start();

    }
}
