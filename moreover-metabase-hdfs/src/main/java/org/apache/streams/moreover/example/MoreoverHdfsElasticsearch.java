package org.apache.streams.moreover.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.builders.LocalStreamBuilder;
import org.apache.streams.core.builders.StreamBuilder;
import org.apache.streams.data.moreover.MoreoverConfigurator;
import org.apache.streams.data.moreover.MoreoverProvider;
import org.apache.streams.elasticsearch.ElasticsearchConfiguration;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.hdfs.*;
import org.apache.streams.moreover.MoreoverConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class MoreoverHdfsElasticsearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(MoreoverHdfsElasticsearch.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args)
    {

        Config hdfs = StreamsConfigurator.config.getConfig("hdfs");
        HdfsConfiguration hdfsConfiguration = HdfsConfigurator.detectConfiguration(hdfs);
        HdfsReaderConfiguration hdfsReaderConfiguration  = mapper.convertValue(hdfsConfiguration, HdfsReaderConfiguration.class);
        hdfsReaderConfiguration.setReaderPath(MoreoverProvider.STREAMS_ID);

        WebHdfsPersistReader hdfsReader = new WebHdfsPersistReader(hdfsReaderConfiguration);

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");
        ElasticsearchConfiguration elasticsearchConfiguration = ElasticsearchConfigurator.detectConfiguration(elasticsearch);
        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration  = mapper.convertValue(elasticsearchConfiguration, ElasticsearchWriterConfiguration.class);
        elasticsearchWriterConfiguration.setIndex("test_moreover");
        elasticsearchWriterConfiguration.setType("moreover");

        ElasticsearchPersistWriter elasticsearchWriter = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>());

        builder.newReadCurrentStream(WebHdfsPersistReader.STREAMS_ID, hdfsReader);
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchWriter, 1, WebHdfsPersistReader.STREAMS_ID);
        builder.start();

    }

}
