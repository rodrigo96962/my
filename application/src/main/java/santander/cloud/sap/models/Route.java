package santander.cloud.sap.models;

import lombok.Builder;

@Builder
@SuppressWarnings("PMD.UnusedPrivateField")
public class Route
{
    private String host;
    private String path;
    private Relationships relationships;

 }
