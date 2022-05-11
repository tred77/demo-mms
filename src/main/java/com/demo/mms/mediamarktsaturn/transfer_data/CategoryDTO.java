package com.demo.mms.mediamarktsaturn.transfer_data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {
    private Long id;
    private String name;
    private CategoryDTO parent;
}
