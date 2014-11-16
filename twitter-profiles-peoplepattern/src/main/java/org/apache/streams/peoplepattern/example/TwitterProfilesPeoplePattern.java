package org.apache.streams.peoplepattern.example;

import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.converter.ActivityConverterProcessor;
import org.apache.streams.converter.ActivityConverterProcessorConfiguration;
import org.apache.streams.converter.TypeConverterProcessor;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.data.ActivityConverterResolver;
import org.apache.streams.data.DocumentClassifier;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.peoplepattern.AccountTypeProcessor;
import org.apache.streams.peoplepattern.DemographicsProcessor;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.processor.TwitterUrlApiProcessor;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.apache.streams.twitter.serializer.TwitterConverterResolver;
import org.apache.streams.twitter.serializer.TwitterDocumentClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterProfilesPeoplePattern {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterProfilesPeoplePattern.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");

        StreamBuilder builder = new LocalStreamBuilder(10);

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterConfigurator.detectTwitterStreamConfiguration(twitter);
        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        TypeConverterProcessor converter = new TypeConverterProcessor(String.class);
        ActivityConverterProcessor activity = new ActivityConverterProcessor(
                new ActivityConverterProcessorConfiguration()
                        .withClassifiers(Lists.newArrayList((DocumentClassifier) TwitterDocumentClassifier.getInstance()))
                        .withResolvers(Lists.newArrayList((ActivityConverterResolver) TwitterConverterResolver.getInstance()))
        );

        TwitterUrlApiProcessor twitterUrlApiProcessor = new TwitterUrlApiProcessor();
        AccountTypeProcessor accountTypeProcessor = new AccountTypeProcessor();
        DemographicsProcessor demographicsProcessor = new DemographicsProcessor();

        builder.newPerpetualStream("provider", stream);
        builder.addStreamsProcessor("converter", converter, 1, "provider");
        builder.addStreamsProcessor("activity", converter, 1, "converter");
        builder.addStreamsProcessor("twitterUrlApiProcessor", twitterUrlApiProcessor, 2, "converter");
        builder.addStreamsProcessor("accountTypeProcessor", accountTypeProcessor, 2, "twitterUrlApiProcessor");
        builder.addStreamsProcessor("demographicsProcessor", demographicsProcessor, 2, "accountTypeProcessor");
        builder.addStreamsPersistWriter("console", new ConsolePersistWriter(), 1, "demographicsProcessor");

        builder.start();
    }
}
