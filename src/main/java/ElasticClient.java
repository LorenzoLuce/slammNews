import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticClient {

    private static ElasticClient istanza;
    private static TransportClient client;

    private ElasticClient() {
        Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).put("client.transport.ignore_cluster_name", true).build();
        this.client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }

    public static ElasticClient getInstance() {
        if (istanza == null) {
            istanza = new ElasticClient();
        }
        return istanza;
    }

    public TransportClient getClient() {
        return client;
    }

}