package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("PMD.UnusedPrivateField")
public class ApplicationDependency
{
    @JsonProperty("xsappname")
    private final String name;
       
    public ApplicationDependency(String name) {
        this.name = name;
    }

  
}
