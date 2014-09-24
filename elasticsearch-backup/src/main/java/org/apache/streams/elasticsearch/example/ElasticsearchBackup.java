package org.apache.streams.elasticsearch.example;

import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistReader;
import org.apache.streams.elasticsearch.ElasticsearchReaderConfiguration;
import org.apache.streams.hdfs.HdfsConfigurator;
import org.apache.streams.hdfs.HdfsWriterConfiguration;
import org.apache.streams.hdfs.WebHdfsPersistWriter;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchBackup {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchBackup.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");
        ElasticsearchReaderConfiguration elasticsearchReaderConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(elasticsearch);

        ElasticsearchPersistReader elasticsearchReader = new ElasticsearchPersistReader(elasticsearchReaderConfiguration);

        Config hdfs = StreamsConfigurator.config.getConfig("hdfs");
        HdfsWriterConfiguration hdfsWriterConfiguration  = HdfsConfigurator.detectWriterConfiguration(hdfs);

        WebHdfsPersistWriter hdfsWriter = new WebHdfsPersistWriter(hdfsWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(2500));

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchReader);
        builder.addStreamsPersistWriter(WebHdfsPersistWriter.STREAMS_ID, hdfsWriter, 1, ElasticsearchPersistReader.STREAMS_ID);
        builder.start();

    }

}
