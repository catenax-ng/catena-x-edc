package net.catenax.edc.hashicorpvault;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Builder
@Getter
public class HashiCorpVaultHealthResponse {

    @Nullable
    private HashiCorpVaultHealthResponsePayload payload;

    private int code;
    private HashiCorpVaultHealthResponseCode codeAsEnum;

    public enum HashiCorpVaultHealthResponseCode {
        UNSPECIFIED, // undefined status codes
        INITIALIZED_UNSEALED_AND_ACTIVE, // status code 200
        UNSEALED_AND_STANDBY, // status code 429
        DISASTER_RECOVERY_MODE_REPLICATION_SECONDARY_AND_ACTIVE, // status code 472
        PERFORMANCE_STANDBY, // status code 473
        NOT_INITIALIZED, // status code 501
        SEALED // status code 503
    }
}
