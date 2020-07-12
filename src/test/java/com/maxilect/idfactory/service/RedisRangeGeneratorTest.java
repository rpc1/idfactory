package com.maxilect.idfactory.service;

import com.maxilect.idfactory.exceptions.RangeOverflowException;
import com.maxilect.idfactory.model.Range;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.boot.autoconfigure.cache.CacheProperties;


class RedisRangeGeneratorTest {

    private RedissonClient client;
    private long batchSize = 10;
    private RangeGenerator rangeGenerator;

    @BeforeEach
    void initService() {
        client = Mockito.mock(RedissonClient.class);
        rangeGenerator = new RedisRangeGenerator(batchSize, client, "key");
    }

    @Test
    void getNextRange_shouldCall_addAndGet_toRedis() {
        RAtomicLong rAtomicLong = Mockito.mock(RAtomicLong.class);
        Mockito.when(client.getAtomicLong(ArgumentMatchers.anyString())).thenReturn(rAtomicLong);
        rangeGenerator.getNextRange();
        Mockito.verify(rAtomicLong).addAndGet(batchSize);
    }

    @Test
    void getNextRange_should_returnValidRange() {
        long rangeMax = 100;
        RAtomicLong rAtomicLong = Mockito.mock(RAtomicLong.class);
        Mockito.when(client.getAtomicLong(ArgumentMatchers.anyString())).thenReturn(rAtomicLong);
        Mockito.when(rAtomicLong.addAndGet(batchSize)).thenReturn(rangeMax);
        Range range = rangeGenerator.getNextRange();
        Assertions.assertEquals(rangeMax, range.getMax());
        Assertions.assertEquals(rangeMax - batchSize, range.getMin());
    }

    @Test
    void getNextRange_throwsException_whenExceeds_long() {
        RAtomicLong rAtomicLong = Mockito.mock(RAtomicLong.class);
        Mockito.when(client.getAtomicLong(ArgumentMatchers.anyString())).thenReturn(rAtomicLong);
        Mockito.when(rAtomicLong.addAndGet(batchSize)).thenThrow(new RedisException("overflow"));
        Assertions.assertThrows(RangeOverflowException.class, () -> rangeGenerator.getNextRange());
    }

}