package org.apache.streams.elasticsearch.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.hdfs.*;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchBackup {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchBackup.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    private static String index;
    private static String type;

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");
        ElasticsearchReaderConfiguration elasticsearchConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(elasticsearch);

        ElasticsearchPersistReader elasticsearchReader = new ElasticsearchPersistReader(elasticsearchConfiguration);

        Config hdfs = StreamsConfigurator.config.getConfig("hdfs");
        HdfsWriterConfiguration hdfsConfiguration = HdfsConfigurator.detectWriterConfiguration(hdfs);

        WebHdfsPersistWriter hdfsWriter = new WebHdfsPersistWriter(hdfsConfiguration);

        StreamBuilder builder = new LocalStreamBuilder(new ConcurrentLinkedQueue<StreamsDatum>());

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchReader);
        builder.addStreamsPersistWriter(WebHdfsPersistReader.STREAMS_ID, hdfsWriter, 1, ElasticsearchPersistReader.STREAMS_ID);
        builder.start();

    }

}
