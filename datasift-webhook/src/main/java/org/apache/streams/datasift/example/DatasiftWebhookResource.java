package com.w2olabs.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Splitter;
import org.apache.streams.core.StreamsDatum;
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

    @POST
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

            for( String item : Splitter.on('\n').split(body)) {
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