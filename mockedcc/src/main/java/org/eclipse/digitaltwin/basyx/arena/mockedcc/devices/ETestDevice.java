package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ETestDevice extends SingleSkillDevice {

    private double etestPosition;
    private boolean etestPassed = false;

    final double etestTotal = 100;
    final double etestStep = 1;
    final long delayPerStep = 200; // ms

    @Async
    @Override
    public Map<String, Object> apply(Map<String, Object> ins) {
        Optional.ofNullable(ins.get("etest_position"))
                .map(String::valueOf)
                .map(Double::parseDouble)
                .ifPresent(t -> {
                    this.etest(t);
                    ins.clear();
                    ins.put("etest_passed", etestPassed);
                });

        return ins;
    }

    public void etest(double etestPosition) {
        doWork(() ->{
            this.etestPosition = etestPosition;

            double etestProgress = 0;
    
            while (etestTotal >= etestProgress) {
                etestProgress += etestStep;
                log("[" + etestProgress / etestTotal * 100 + "%] Testing...");
                doSleep(delayPerStep);
            }
            
            etestPassed = true;
        }, "Electrical Test");
    }

    @Override
    public String getDeviceName() {
        return "ETestDevice";
    }

    public double getEtestPosition() {
        return etestPosition;
    }

    public boolean isEtestPassed() {
        return etestPassed;
    }

}