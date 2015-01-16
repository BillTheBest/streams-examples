/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.streams.elasticsearch.example;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistReader;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistDeleter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.elasticsearch.processor.DatumFromMetadataProcessor;
import org.apache.streams.elasticsearch.processor.DocumentToMetadataProcessor;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchDeleteFromMetadataFile implements Runnable {

    public final static String STREAMS_ID = "ElasticsearchDeleteFromMetadataFile";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDeleteFromMetadataFile.class);

    public static void main(String[] args)
    {
        ElasticsearchDeleteFromMetadataFile job = new ElasticsearchDeleteFromMetadataFile();
        (new Thread(job)).start();

    }

    @Override
    public void run() {

        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");

        ElasticsearchWriterConfiguration elasticsearchConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(elasticsearch);

        ElasticsearchPersistDeleter elasticsearchPersistDeleter = new ElasticsearchPersistDeleter(elasticsearchConfiguration);

        Map<String, Object> streamConfig = Maps.newHashMap();
        streamConfig.put(LocalStreamBuilder.TIMEOUT_KEY, 20 * 60 * 1000);
        StreamBuilder builder = new LocalStreamBuilder(1000, streamConfig);

        //builder.newReadCurrentStream("console", new ConsolePersistReader());
        builder.newPerpetualStream("console", new ConsolePersistReader());
        builder.addStreamsProcessor(DocumentToMetadataProcessor.STREAMS_ID, new DocumentToMetadataProcessor(), 1, "console");
        builder.addStreamsPersistWriter(ElasticsearchPersistDeleter.STREAMS_ID, elasticsearchPersistDeleter, 1, DocumentToMetadataProcessor.STREAMS_ID);
        builder.start();

    }



}
