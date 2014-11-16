package org.apache.streams.twitter.example;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.converter.ActivityConverterProcessor;
import org.apache.streams.converter.ActivityConverterProcessorConfiguration;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.data.ActivityConverterResolver;
import org.apache.streams.data.DocumentClassifier;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.apache.streams.twitter.serializer.TwitterConverterResolver;
import org.apache.streams.twitter.serializer.TwitterDocumentClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        ActivityConverterProcessor converter = new ActivityConverterProcessor(
                new ActivityConverterProcessorConfiguration()
                        .withClassifiers(Lists.newArrayList((DocumentClassifier) TwitterDocumentClassifier.getInstance()))
                        .withResolvers(Lists.newArrayList((ActivityConverterResolver) TwitterConverterResolver.getInstance()))
        );
        ConsolePersistWriter console = new ConsolePersistWriter();

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);
        streamConfig.put("monitoring_broadcast_interval_ms", -1);

        StreamBuilder builder = new LocalStreamBuilder(100, streamConfig);
//        StreamBuilder builder = new LocalStreamBuilder(100);

        builder.newPerpetualStream(TwitterStreamProvider.STREAMS_ID, stream);
        builder.addStreamsProcessor("activity", converter, 1, TwitterStreamProvider.STREAMS_ID);
        builder.addStreamsPersistWriter("console", console, 1, "activity");
        builder.start();

    }
}
