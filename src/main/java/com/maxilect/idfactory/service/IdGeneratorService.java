package com.maxilect.idfactory.service;

import com.maxilect.idfactory.model.IdValue;
import com.maxilect.idfactory.model.Range;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGeneratorService implements IdGenerator {

    private final RangeGenerator rangeGenerator;

    private AtomicLong currentIdValue;
    private volatile Range currentRange;

    public IdGeneratorService(RangeGenerator rangeGenerator) {
        this.rangeGenerator = rangeGenerator;
        this.currentIdValue = new AtomicLong(0);
        this.currentRange = new Range(0, 1, 0);
    }


    @Override
    public IdValue generateUniqueId() {
        long id = currentIdValue.addAndGet(currentRange.getStep());
        if (id > currentRange.getMax() || id < currentRange.getMin()) {
            updateRange();
            return generateUniqueId();
        }
        return new IdValue(id);
    }

    private synchronized void updateRange() {
        if (currentRange.getMax() > currentIdValue.get()) {
            return;
        }
        currentRange = rangeGenerator.getNextRange();
        currentIdValue.set(currentRange.getMin());
    }
}
