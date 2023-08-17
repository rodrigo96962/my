package santander.cloud.sap.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@SuppressWarnings("PMD.UnusedPrivateField")
public class CFData {

    private String domainId;
    private String appId;
    private String domainUrl;
    private String spaceId;

   
}
