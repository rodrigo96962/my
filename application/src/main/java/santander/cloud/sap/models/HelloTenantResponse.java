package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("PMD.UnusedPrivateField")
public class HelloTenantResponse
{
    @JsonProperty("name")
    private final String name;
    @JsonProperty("id")
    private final String id;
    @JsonProperty("subdomain")
    private final String subdomain;
    @JsonProperty("user")
    private final String user;
    
    public HelloTenantResponse(String name, String id, String subdomain, String user) {
        this.name = name;
        this.id = id;
        this.subdomain = subdomain;
        this.user = user;
    }

  
}
