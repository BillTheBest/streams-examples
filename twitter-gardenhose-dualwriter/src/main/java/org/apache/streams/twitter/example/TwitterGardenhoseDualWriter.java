package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfiguration;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.hdfs.HdfsConfiguration;
import org.apache.streams.hdfs.HdfsConfigurator;
import org.apache.streams.hdfs.HdfsWriterConfiguration;
import org.apache.streams.hdfs.WebHdfsPersistWriter;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.processor.TwitterTypeConverter;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.apache.streams.twitter.provider.TwitterTimelineProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterGardenhoseDualWriter {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterGardenhoseDualWriter.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");
        Config hdfs = StreamsConfigurator.config.getConfig("hdfs");
        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        StreamBuilder builder = new LocalStreamBuilder();

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterConfigurator.detectTwitterStreamConfiguration(twitter);
        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        TwitterTypeConverter converter = new TwitterTypeConverter(String.class, Activity.class);

        HdfsWriterConfiguration hdfsWriterConfiguration = HdfsConfigurator.detectWriterConfiguration(hdfs);
        hdfsWriterConfiguration.setWriterFilePrefix("data");

        WebHdfsPersistWriter hdfsWriter = new WebHdfsPersistWriter(hdfsWriterConfiguration);

        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        ElasticsearchPersistWriter elasticsearchWriter = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        builder.newPerpetualStream("provider", stream);
        builder.addStreamsProcessor("converter", converter, 4, "provider");
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchWriter, 1, "converter");
        builder.addStreamsPersistWriter(WebHdfsPersistWriter.STREAMS_ID, hdfsWriter, 1, "converter");

        builder.start();
    }
}
