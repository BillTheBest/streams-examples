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

package org.apache.streams.gmail.example;

import com.google.gmail.GMailConfiguration;
import com.google.gmail.GMailConfigurator;
import com.google.gmail.provider.GMailProvider;
import com.googlecode.gmail4j.GmailMessage;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class GMailExportConsole {

    private final static Logger LOGGER = LoggerFactory.getLogger(GMailExportConsole.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config gmail = StreamsConfigurator.config.getConfig("gmail");

        GMailConfiguration gmailConfiguration = GMailConfigurator.detectConfiguration(gmail);

        GMailProvider stream = new GMailProvider(gmailConfiguration, GmailMessage.class);
        ConsolePersistWriter console = new ConsolePersistWriter();

        StreamBuilder builder = new LocalStreamBuilder();

        builder.newPerpetualStream("gmail", stream);
        builder.addStreamsPersistWriter("console", console, 1, "gmail");
        builder.start();

        // run until user exits
    }
}
