package sg.ntuchealth.yoda.edge.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.service.model.LinkIdResponse;

@Component
public class CacheClient {

  public static final String LINKIDTOKEN = "LINK_ID_TOKEN";
  private final HazelcastInstance hazelcastInstance =
      Hazelcast.newHazelcastInstance(createConfig());

  public Config createConfig() {
    Config config = new Config();
    config.addMapConfig(mapConfig());
    return config;
  }

  private MapConfig mapConfig() {
    return new MapConfig(LINKIDTOKEN);
  }

  public LinkIdResponse put(String key, LinkIdResponse linkIdResponse) {
    IMap<String, LinkIdResponse> map = hazelcastInstance.getMap(LINKIDTOKEN);
    return map.put(key, linkIdResponse);
  }

  public LinkIdResponse get(String key) {
    IMap<String, LinkIdResponse> map = hazelcastInstance.getMap(LINKIDTOKEN);
    return map.get(key);
  }
}
