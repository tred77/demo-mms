package com.demo.mms.mediamarktsaturn.transfer_data;

import com.demo.mms.mediamarktsaturn.domain_value.OnlineStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private Long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private OnlineStatus onlineStatus;
}
