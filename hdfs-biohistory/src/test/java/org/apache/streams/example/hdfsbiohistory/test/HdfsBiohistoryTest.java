/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

package org.apache.streams.example.hdfsbiohistory.test;

import org.apache.commons.io.FileUtils;
import org.apache.pig.pigunit.PigTest;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.processor.TwitterTypeConverter;
import org.apache.streams.twitter.serializer.TwitterJsonActivitySerializer;
import org.apache.streams.twitter.serializer.TwitterJsonTweetActivitySerializer;
import org.apache.tools.ant.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * These are tests for StreamsProcessDocumentExec
 */
public class HdfsBiohistoryTest {

    @Ignore
    @Test
    public void testHdfsBiohistory() throws Exception {

        PigTest test;
        test = new PigTest("src/test/resources/hdfsbiohistorytest.pig");
        test.unoverride("STORE");
        test.runScript();

        File expected = new File("src/test/resources/hdfsbiohistorytestout.txt");
        File actual = new File("target/timestampedbios/part-m-00000");

        assertEquals(FileUtils.readFileToString(expected), FileUtils.readFileToString(actual));
    }

}
