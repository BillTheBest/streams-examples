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
