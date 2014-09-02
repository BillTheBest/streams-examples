package org.apache.streams.twitter.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.hdfs.HdfsConfiguration;
import org.apache.streams.hdfs.HdfsConfigurator;
import org.apache.streams.hdfs.HdfsWriterConfiguration;
import org.apache.streams.hdfs.WebHdfsPersistWriter;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.TwitterUserInformationConfiguration;
import org.apache.streams.twitter.provider.TwitterTimelineProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterHistoryHdfs {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterHistoryHdfs.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");
        TwitterUserInformationConfiguration twitterUserInformationConfiguration = mapper.convertValue(twitter.root().render(ConfigRenderOptions.concise()), TwitterUserInformationConfiguration.class);

        Config hdfs = StreamsConfigurator.config.getConfig("hdfs");
        HdfsConfiguration hdfsConfiguration = HdfsConfigurator.detectConfiguration(hdfs);
        HdfsWriterConfiguration hdfsWriterConfiguration  = mapper.convertValue(hdfsConfiguration, HdfsWriterConfiguration.class);
        hdfsWriterConfiguration.setWriterPath(TwitterTimelineProvider.STREAMS_ID);
        hdfsWriterConfiguration.setWriterFilePrefix("data");

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>());

        TwitterTimelineProvider provider = new TwitterTimelineProvider(twitterUserInformationConfiguration, Activity.class);
        WebHdfsPersistWriter writer = new WebHdfsPersistWriter(hdfsWriterConfiguration);

        builder.newReadCurrentStream("provider", provider);
        builder.addStreamsPersistWriter("writer", writer, 1, "provider");
        builder.start();

    }
}
