package org.apache.streams.examples.intsagram.recentmedia;

import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.instagram.provider.InstagramRecentMediaProvider;
import org.apache.streams.local.builders.LocalStreamBuilder;

/**
 * Created by rebanks on 7/11/14.
 */
public class InstagramRecentMediaConsole {

    public static void main(String[] args) {
        StreamBuilder builder = new LocalStreamBuilder();
        builder.newPerpetualStream("instagram", new InstagramRecentMediaProvider());
        builder.addStreamsPersistWriter("console", new ConsolePersistWriter(), 1, "instagram");
        builder.start();
    }


}
