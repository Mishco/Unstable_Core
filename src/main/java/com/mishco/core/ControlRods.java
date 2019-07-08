package com.mishco.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ControlRods {

    @Getter
    private Integer id;
    @Getter
    @Setter
    private Double percentInCore;

}
