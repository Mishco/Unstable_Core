package com.mishco;

import com.mishco.core.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mishco.helper.Util.MAX_CORE_POWER;

public class Plant {
    private List<Core> coreList = new ArrayList<>();

    public void addCoreToPlant(Core core) {
        coreList.add(core);
    }

    private void changePower() {
        coreList.forEach(core -> core.getGenerator().setOutputPower(Double.valueOf(new Random().nextInt(MAX_CORE_POWER))));
    }

    public void makeControlInAllCores() {
        coreList.forEach(core -> System.out.println(core.getGenerator().getOutputPower()));
        changePower();
    }

    public int isSomeDisaster() {
        for (Core core : coreList) {
            if (core.getTemperatureInCore() > (MAX_CORE_POWER)) {
                return 1;
            }
        }
        return 0;
    }

    public List<Core> getCoreList() {
        return coreList;
    }
}
