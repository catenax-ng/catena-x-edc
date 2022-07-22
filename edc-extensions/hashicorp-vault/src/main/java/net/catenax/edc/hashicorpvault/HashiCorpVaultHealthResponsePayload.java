package net.catenax.edc.hashicorpvault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class HashiCorpVaultHealthResponsePayload {
    @JsonProperty("initialized")
    private boolean isInitialized;
    @JsonProperty("sealed")
    private boolean isSealed;
    @JsonProperty("standby")
    private boolean isStandby;
    @JsonProperty("performance_standby")
    private boolean isPerformanceStandby;
    @JsonProperty("replication_performance_mode")
    private String replicationPerformanceMode;
    @JsonProperty("replication_dr_mode")
    private String replicationDrMode;
    @JsonProperty("server_time_utc")
    private long serverTimeUtc;
    @JsonProperty("version")
    private String version;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("cluster_id")
    private String clusterId;
}
