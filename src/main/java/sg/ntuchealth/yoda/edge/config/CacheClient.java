package sg.ntuchealth.yoda.edge.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.service.model.LinkIdResponse;

@Component
public
class CacheClient {

    public static final String LinkIdToken= "linkIdToken";
    private final HazelcastInstance hazelcastInstance
            = Hazelcast.newHazelcastInstance(createConfig());

    public Config createConfig() {
        Config config = new Config();
        config.addMapConfig(mapConfig());
        return config;
    }

    private MapConfig mapConfig() {
        MapConfig mapConfig = new MapConfig(LinkIdToken);
        //mapConfig.setTimeToLiveSeconds(360);
        return mapConfig;
    }

    public LinkIdResponse put(String key, LinkIdResponse linkIdResponse) {
        IMap<String, LinkIdResponse> map = hazelcastInstance.getMap(LinkIdToken);
        return map.put(key, linkIdResponse);
    }

    public LinkIdResponse get(String key) {
        IMap<String, LinkIdResponse> map = hazelcastInstance.getMap(LinkIdToken);
        return map.get(key);
    }

}
