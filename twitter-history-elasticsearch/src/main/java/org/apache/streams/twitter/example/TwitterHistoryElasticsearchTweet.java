package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.converter.TypeConverterProcessor;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.TwitterUserInformationConfiguration;
import org.apache.streams.twitter.pojo.Tweet;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterTimelineProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterHistoryElasticsearchTweet {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterHistoryElasticsearchTweet.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");
        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        TwitterUserInformationConfiguration twitterUserInformationConfiguration = TwitterConfigurator.detectTwitterUserInformationConfiguration(twitter);
        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        TwitterTimelineProvider provider = new TwitterTimelineProvider(twitterUserInformationConfiguration, String.class);
        TypeConverterProcessor converter = new TypeConverterProcessor(Tweet.class);
        ElasticsearchPersistWriter writer = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder();

        builder.newReadCurrentStream("provider", provider);
        builder.addStreamsProcessor("converter", converter, 2, "provider");
        builder.addStreamsPersistWriter("writer", writer, 2, "converter");
        builder.start();

    }

}
