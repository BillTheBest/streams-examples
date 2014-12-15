package org.apache.streams.datasift.example;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.datasift.DatasiftConfiguration;
import org.apache.streams.datasift.processor.DatasiftTypeConverterProcessor;
import org.apache.streams.datasift.provider.DatasiftStreamConfigurator;
import org.apache.streams.datasift.provider.DatasiftStreamProvider;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.regex.RegexMentionsExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by sblackmon on 12/10/13.
 */
public class DatasiftInstagramElasticsearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(DatasiftInstagramElasticsearch.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config datasift = StreamsConfigurator.config.getConfig("datasift");
        DatasiftConfiguration datasiftConfiguration = DatasiftStreamConfigurator.detectConfiguration(datasift);

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");
        ElasticsearchWriterConfiguration elasticsearchWriterConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000 * 1000);

        StreamBuilder builder = new LocalStreamBuilder(100, streamConfig);

        DatasiftStreamProvider stream = new DatasiftStreamProvider(new DatasiftStreamProvider.DeleteHandler(), datasiftConfiguration);
        DatasiftTypeConverterProcessor datasiftTypeConverter = new DatasiftTypeConverterProcessor(Activity.class);
        RegexMentionsExtractor regexMentionsExtractor = new RegexMentionsExtractor();
        ElasticsearchPersistWriter writer = new ElasticsearchPersistWriter(elasticsearchWriterConfiguration);

        builder.newPerpetualStream("stream", stream);
        builder.addStreamsProcessor("converter", datasiftTypeConverter, 2, "stream");
        builder.addStreamsProcessor("RegexMentionsExtractor", regexMentionsExtractor, 2, "CleanAdditionalProperties");
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, writer, 1, "RegexMentionsExtractor");
        builder.start();

    }
}
