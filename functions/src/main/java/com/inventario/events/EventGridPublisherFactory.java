// EventFactory.java
package com.inventario.events;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.inventario.config.AppConfig;

import java.util.Map;

public class EventGridPublisherFactory {

    private static class Holder {
        private static final EventGridPublisherClient<EventGridEvent> CLIENT = build();
        private static EventGridPublisherClient<EventGridEvent> build() {
            String endpoint =  AppConfig.eventGridTopicEndpoint();
            String key = AppConfig.eventGridAccessKey();
            
            if (endpoint == null || key == null) {
                throw new IllegalStateException("Faltan EVENTGRID_TOPIC_ENDPOINT o EVENTGRID_ACCESS_KEY");
            }
            return new EventGridPublisherClientBuilder()
                    .endpoint(endpoint)
                    .credential(new AzureKeyCredential(key))
                    .buildEventGridEventPublisherClient();
        }
    }


    private static EventGridPublisherClient<EventGridEvent> client() {
        return Holder.CLIENT;
    }


    public static void publishCrud(String aggregate, CrudAction action, String subject, Object data) {
        // type de la forma "com.inventario.mascota.created"
        String eventType = String.format("com.inventario.%s.%s",
                aggregate.toLowerCase(), action.name().toLowerCase());

        EventGridEvent ev = new EventGridEvent(
                subject,         // subject
                eventType,       // eventType
                BinaryData.fromObject(data != null ? data : Map.of()),
                "1.0"            // dataVersion
        );
        client().sendEvent(ev);
    }
   
}
