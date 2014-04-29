package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.hbase.HbasePersistWriter;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.processor.TwitterProfileProcessor;
import org.apache.streams.twitter.provider.TwitterStreamConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterProfilesHbase {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterProfilesHbase.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterStreamConfigurator.detectConfiguration(twitter);

        TwitterStreamProvider provider = new TwitterStreamProvider(twitterStreamConfiguration);
        TwitterProfileProcessor profile = new TwitterProfileProcessor();
        HbasePersistWriter writer = new HbasePersistWriter();

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(100));

        builder.newPerpetualStream(TwitterStreamProvider.STREAMS_ID , provider);
        builder.addStreamsProcessor("profile", profile, 1, TwitterStreamProvider.STREAMS_ID);
        builder.addStreamsPersistWriter("hbase", writer, 1, "profile");
        builder.start();
    }
}
