package org.tony.kafka.produce.topic;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Arrays;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.kafka.produce.topic
 */
public class TopicService {

    public static void createTopics(){
        try {
            AdminClient adminClient = AdminClientService.createClient();
            NewTopic newTopic = new NewTopic("topic-test", 1, (short) 1);
            CreateTopicsOptions createTopicsOptions = new CreateTopicsOptions();
            createTopicsOptions.timeoutMs(1_000);
            adminClient.createTopics(Arrays.asList(newTopic), createTopicsOptions);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

}
