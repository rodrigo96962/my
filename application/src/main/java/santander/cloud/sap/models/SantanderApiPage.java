package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SantanderApiPage {
    @JsonProperty("_limit")
    private int limit;
    @JsonProperty("_offset")
    private int offset;
    @JsonProperty("_pageNumber")
    private int pageNumber;
    @JsonProperty("_pageElements")
    private int pageElements;
    @JsonProperty("_totalPages")
    private int totalPages;
    @JsonProperty("_totalElements")
    private int totalElements;
}
