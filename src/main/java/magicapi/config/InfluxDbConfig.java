package magicapi.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class InfluxDbConfig {
    @Value("${influxdb2.url}")
    private String url2;

    @Value("${influxdb2.token}")
    private String token2;

    @Value("${influxdb2.org}")
    private String org2;

    @Value("${influxdb2.bucket}")
    private String bucket2;

    @Bean
    public InfluxDBClient influxDb2() {
        return InfluxDBClientFactory.create(url2, token2.toCharArray(), org2, bucket2);
    }

    @Bean
    public QueryApi queryApiOfInfluxDb2(InfluxDBClient influxDb2) {
//        List<FluxTable> query = influxDb2.getQueryApi().query("""
//                from(bucket: "jkscada")
//                    |> range(start: -3m)
//                    |> filter(fn: (r) => r["_measurement"] == "data")
//                    |> filter(fn: (r) => r["read"] == "0")
//                """);
        return influxDb2.getQueryApi();
    }

    @Bean
    public WriteApi writeApiOfInfluxDb2(InfluxDBClient influxDb2) {
        return influxDb2.makeWriteApi();
    }

}
