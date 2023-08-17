package santander.cloud.sap.models;

import java.util.List;

import lombok.Builder;

@Builder
@SuppressWarnings("PMD.UnusedPrivateField")
public class Destinations {
    private List<Destination> destinations;
}
