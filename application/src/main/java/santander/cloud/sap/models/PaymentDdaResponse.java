package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import santander.cloud.sap.converter.ObjectArrayToPayentDdaListConverter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDdaResponse {
    @JsonProperty("_pageable")
    private SantanderApiPage page;

    @JsonProperty("_content")
    private List<PaymentDda> paymentDdaList;
}