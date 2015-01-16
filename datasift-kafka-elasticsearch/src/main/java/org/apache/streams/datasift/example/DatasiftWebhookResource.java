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

package org.apache.streams.datasift.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Splitter;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.datasift.Datasift;
import org.apache.streams.datasift.DatasiftPush;
import org.apache.streams.datasift.provider.DatasiftPushProvider;
import org.apache.streams.datasift.util.StreamsDatasiftMapper;
import org.apache.streams.util.ComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Scanner;
import java.util.regex.Pattern;

@Resource
@Path("/streams/webhooks/datasift")
@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
public class DatasiftWebhookResource extends DatasiftPushProvider
{
    public DatasiftWebhookResource() {
    }

    private static final Logger log = LoggerFactory
            .getLogger(DatasiftWebhookResource.class);

    private static ObjectMapper mapper = StreamsDatasiftMapper.getInstance();

    private static Pattern newLinePattern = Pattern.compile("(\\r\\n?|\\n)", Pattern.MULTILINE);

    @POST
    @Path("json_meta")
    public Response datasift_json_meta(@Context HttpHeaders headers,
                                       String body) {

        //log.debug(headers.toString(), headers);

        //log.debug(body.toString(), body);

        ObjectNode response = mapper.createObjectNode();

        if (body.equalsIgnoreCase("{}")) {

            Boolean success = true;

            response.put("success", success);

            return Response.status(200).entity(response).build();
        }

        try {

            DatasiftPush interactions = mapper.readValue(body, DatasiftPush.class);

            for( Datasift item : interactions.getInteractions()) {

                String json = mapper.writeValueAsString(item);

                StreamsDatum datum = new StreamsDatum(json, item.getInteraction().getId(), item.getInteraction().getCreatedAt());

                lock.writeLock().lock();
                ComponentUtils.offerUntilSuccess(datum, providerQueue);
                lock.writeLock().unlock();
            }

            log.info("interactionQueue: " + providerQueue.size());

            Boolean success = true;

            response.put("success", success);

            return Response.status(200).entity(response).build();

        } catch (Exception e) {
            log.warn(e.toString(), e);
        }

        return Response.status(500).build();
    }

    @POST
    @Path("json_new_line")
    public Response datasift_json_new_line(@Context HttpHeaders headers,
                                           String body) {

        //log.debug(headers.toString(), headers);

        //log.debug(body.toString(), body);

        ObjectNode response = mapper.createObjectNode();

        if (body.equalsIgnoreCase("{}")) {

            Boolean success = true;

            response.put("success", success);

            return Response.status(200).entity(response).build();
        }

        try {

            Scanner scanner = new Scanner(body);

            while( scanner.hasNext() ) {
                String item = scanner.next();

                StreamsDatum datum = new StreamsDatum(item);

                lock.writeLock().lock();
                ComponentUtils.offerUntilSuccess(datum, providerQueue);
                lock.writeLock().unlock();

            }

            log.info("interactionQueue: " + providerQueue.size());

            Boolean success = true;

            response.put("success", success);

            return Response.status(200).entity(response).build();

        } catch (Exception e) {
            log.warn(e.toString(), e);
        }

        return Response.status(500).build();
    }


}