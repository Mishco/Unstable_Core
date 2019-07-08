package com.mishco.generator;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Generator {

    /**
     * Amount of water in tank
     */
    private Double capacityWater;

    /**
     * Limit of maximum water in one reactor
     */
    private Double capacityReactor;
    private Double amountOfWaterPerSecond;
    private Double temperatureIncomingWater;
    private Double temperatureOutComingWater;

}
