package com.maxilect.idfactory.service;

import com.maxilect.idfactory.model.IdValue;
import com.maxilect.idfactory.model.Range;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class IdGeneratorServiceTest {

    @Test
    void generateUniqueId_shouldReturnNextValueAfterMin() {
        Range range = new Range(10, 1, 100);
        RangeGenerator rangeGenerator = Mockito.mock(RangeGenerator.class);
        Mockito.when(rangeGenerator.getNextRange()).thenReturn(range);
        IdGenerator idGenerator = new IdGeneratorService(rangeGenerator);
        IdValue id = idGenerator.generateUniqueId();
        Assertions.assertEquals(range.getMin() + range.getStep(), id.getId());
    }

    @Test
    void generateUniqueId_allIdShouldBeInRange() {
        Range range = new Range(10, 1, 100);
        RangeGenerator rangeGenerator = Mockito.mock(RangeGenerator.class);
        Mockito.when(rangeGenerator.getNextRange()).thenReturn(range);
        IdGenerator idGenerator = new IdGeneratorService(rangeGenerator);
        IdValue id;
        for (long i = range.getMin(); i < range.getMax(); i++) {
            id = idGenerator.generateUniqueId();
            Assertions.assertTrue(id.getId() > range.getMin() && id.getId() <= range.getMax());
        }
    }

}