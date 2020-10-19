package org.tony.kafka.produce.topic;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.Objects;
import java.util.Properties;

/**
 * @ProjectName kafka-example
 * @PackageName org.tony.kafka.produce.topic
 */
public class AdminClientService {

    private static final String BROKER = "10.0.20.197:9092";

    private static volatile AdminClient adminClient = null;

    public static synchronized AdminClient createClient(){
        if(Objects.isNull(adminClient)){
            Properties properties = new Properties();
            properties.put("bootstrap.servers",BROKER);
            adminClient = AdminClient.create(properties);
            return adminClient;
        }else{
            return adminClient;
        }
    }

}
