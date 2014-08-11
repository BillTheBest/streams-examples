package org.apache.streams.datasift.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsProcessor;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * HACK PROCESSOR.  Changes need to be made in apache streams to fix this issue long term.
 */
public class RemoveAdditionalPropertiesHackProcessor implements StreamsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveAdditionalPropertiesHackProcessor.class);

    private static final String ADDITIONAL_PROPERTIES = "additionalProperties";
    private static final String EXTENSIONS = "extensions";

    private ObjectMapper mapper;

    @Override
    public List<StreamsDatum> process(StreamsDatum datum) {
        List<StreamsDatum> result = Lists.newLinkedList();
        ObjectNode activity = this.mapper.convertValue(datum.getDocument(), ObjectNode.class);
        ObjectNode actor = (ObjectNode) activity.get("actor");
        if(actor.has(ADDITIONAL_PROPERTIES)) {
            JsonNode extensions = actor.get(ADDITIONAL_PROPERTIES).get(EXTENSIONS);
            actor.remove(ADDITIONAL_PROPERTIES);
            if(extensions != null)
                actor.put(EXTENSIONS, extensions);
        }
        if(activity.has(ADDITIONAL_PROPERTIES)) {
            JsonNode extensions = activity.get(ADDITIONAL_PROPERTIES).get(EXTENSIONS);
            activity.remove(ADDITIONAL_PROPERTIES);
            if(extensions != null)
                activity.put(EXTENSIONS, extensions);
        }
        try {
            datum.setDocument(this.mapper.writeValueAsString(activity));
        } catch (Exception e) {
            LOGGER.error("Exception removing additionalProperties : {}", e);
        }
        result.add(datum);
        return result;
    }

    @Override
    public void prepare(Object o) {
        this.mapper = StreamsJacksonMapper.getInstance();
    }

    @Override
    public void cleanUp() {

    }
}
