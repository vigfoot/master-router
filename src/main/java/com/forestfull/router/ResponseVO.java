package com.forestfull.router;

import com.forestfull.router.config.DATA_TYPE;
import lombok.Builder;

@Builder
public class ResponseVO {
    public DATA_TYPE dataType;
    public Object contents;

}