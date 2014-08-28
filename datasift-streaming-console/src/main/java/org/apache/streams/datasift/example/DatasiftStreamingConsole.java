package org.apache.streams.datasift.example;

import com.datasift.client.stream.StreamEventListener;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.datasift.DatasiftConfiguration;
import org.apache.streams.datasift.provider.DatasiftStreamConfigurator;
import org.apache.streams.datasift.provider.DatasiftStreamProvider;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class DatasiftStreamingConsole {

    private final static Logger LOGGER = LoggerFactory.getLogger(DatasiftStreamingConsole.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config datasift = StreamsConfigurator.config.getConfig("datasift");

        DatasiftConfiguration datasiftConfiguration = DatasiftStreamConfigurator.detectConfiguration(datasift);

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(100));

        DatasiftStreamProvider stream = new DatasiftStreamProvider(new DatasiftStreamProvider.DeleteHandler(), datasiftConfiguration);
        ConsolePersistWriter writer = new ConsolePersistWriter();

        builder.newPerpetualStream("stream", stream);
        builder.addStreamsPersistWriter("console", writer, 1, "stream");
        builder.start();

    }
}
