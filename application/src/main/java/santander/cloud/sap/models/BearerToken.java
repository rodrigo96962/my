package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BearerToken {

    private String access_token;
    private long expires_in;
    private String token_type;

    @JsonProperty("not-before-policy")
    private long notBeforePolicy;
    private String session_state;
    private String scope;
}
