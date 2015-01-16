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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsProcessor;
import org.apache.streams.data.util.ActivityUtil;
import org.apache.streams.data.util.JsonUtil;
import org.apache.streams.elasticsearch.*;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchScriptTagger {

    public final static String STREAMS_ID = "ElasticsearchScriptTag";

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchScriptTagger.class);

    private final static ObjectMapper mapper = StreamsJacksonMapper.getInstance();

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

        ElasticsearchReaderConfiguration elasticsearchSourceConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(source);

        ElasticsearchPersistReader elasticsearchPersistReader = new ElasticsearchPersistReader(elasticsearchSourceConfiguration);

        EnsureTagArrayProcessor ensureTagArrayProcessor = new EnsureTagArrayProcessor();

        ElasticsearchWriterConfiguration elasticsearchDestinationConfiguration = ElasticsearchConfigurator.detectWriterConfiguration(destination);

        ElasticsearchPersistWriter elasticsearchPersistWriter = new ElasticsearchPersistUpdater(elasticsearchDestinationConfiguration);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(1000));

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchPersistReader);
        builder.addStreamsProcessor(EnsureTagArrayProcessor.STREAMS_ID, ensureTagArrayProcessor, 3, ElasticsearchPersistReader.STREAMS_ID);
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, elasticsearchPersistWriter, 1, EnsureTagArrayProcessor.STREAMS_ID);
        builder.start();

    }

    private static class EnsureTagArrayProcessor implements StreamsProcessor {

        public static final String STREAMS_ID = "EnsureTagArrayProcessor";

        @Override
        public List<StreamsDatum> process(StreamsDatum entry) {
            List<StreamsDatum> result = Lists.newArrayList();
            ObjectNode node = mapper.convertValue(entry.getDocument(), ObjectNode.class);
            JsonUtil.ensureArray(node, ActivityUtil.EXTENSION_PROPERTY + ".tags");
            result.add(entry);
            return result;
        }

        @Override
        public void prepare(Object configurationObject) {

        }

        @Override
        public void cleanUp() {

        }

    }

}
