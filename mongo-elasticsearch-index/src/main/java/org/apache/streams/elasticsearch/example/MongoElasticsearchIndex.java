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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistReader;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.mongo.MongoConfiguration;
import org.apache.streams.mongo.MongoPersistReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class MongoElasticsearchIndex {

    public final static String STREAMS_ID = "MongoElasticsearchIndex";

    private final static Logger LOGGER = LoggerFactory.getLogger(MongoElasticsearchIndex.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    protected ListeningExecutorService executor = MoreExecutors.listeningDecorator(newFixedThreadPoolWithQueueSize(5, 20));

    private static ExecutorService newFixedThreadPoolWithQueueSize(int nThreads, int queueSize) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config reindex = StreamsConfigurator.config.getConfig("reindex");

        Config source = reindex.getConfig("source");
        Config destination = reindex.getConfig("destination");

        MongoConfiguration mongoConfiguration = null;
        try {
            mongoConfiguration = mapper.readValue(source.root().render(ConfigRenderOptions.concise()), MongoConfiguration.class);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        Preconditions.checkNotNull(mongoConfiguration);

        ElasticsearchWriterConfiguration elasticsearchConfiguration;
        try {
            elasticsearchConfiguration = mapper.readValue(destination.root().render(ConfigRenderOptions.concise()), ElasticsearchWriterConfiguration.class);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        Preconditions.checkNotNull(elasticsearchConfiguration);

        MongoPersistReader mongoPersistReader = new MongoPersistReader(mongoConfiguration);
        ElasticsearchPersistWriter elasticsearchPersistWriter = new ElasticsearchPersistWriter(elasticsearchConfiguration);

        StreamBuilder builder = new LocalStreamBuilder(1000);

        builder.newPerpetualStream(MongoPersistReader.STREAMS_ID, mongoPersistReader);
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchPersistWriter, 1, MongoPersistReader.STREAMS_ID);
        builder.start();

    }

}
