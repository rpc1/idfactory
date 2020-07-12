package com.maxilect.idfactory.model;

public class Range {
    final long min;
    final long step;
    final long max;

    public Range(long min, long step, long max) {
        this.min = min;
        this.step = step;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getStep() {
        return step;
    }

    public long getMax() {
        return max;
    }
}
