package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import org.apache.streams.components.http.SimpleHTTPGetProcessor;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.json.JsonPathExtractor;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.processor.TwitterTypeConverter;
import org.apache.streams.twitter.processor.TwitterUrlApiProcessor;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.apache.streams.urls.LinkExpanderProcessor;
import org.apache.streams.urls.LinkResolverProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterUserstreamLinksElasticsearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterUserstreamLinksElasticsearch.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");
        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterConfigurator.detectTwitterStreamConfiguration(twitter);
        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(100));

        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        TwitterTypeConverter converter = new TwitterTypeConverter(ObjectNode.class, Activity.class);
        LinkResolverProcessor resolver = new LinkResolverProcessor();
        LinkExpanderProcessor expander = new LinkExpanderProcessor();
        JsonPathExtractor extractor = new JsonPathExtractor("$.object.extensions.link_expander");
        TwitterUrlApiProcessor urlApiProcessor = new TwitterUrlApiProcessor();

        ElasticsearchPersistWriter writer = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        builder.newPerpetualStream(TwitterStreamProvider.STREAMS_ID, stream);
        builder.addStreamsProcessor("converter", converter, 2, TwitterStreamProvider.STREAMS_ID);
        builder.addStreamsProcessor("resolver", resolver, 2, "converter");
        builder.addStreamsProcessor("expander", expander, 2, "resolver");
        builder.addStreamsProcessor("extractor", extractor, 1, "extractor");
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, writer, 1, "urlApiProcessor");
        builder.start();

    }
}
