package com.maxilect.idfactory.service;

import com.maxilect.idfactory.exceptions.RangeGeneratorException;
import com.maxilect.idfactory.exceptions.RangeOverflowException;
import com.maxilect.idfactory.model.Range;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class RedisRangeGenerator implements RangeGenerator {

    private final RedissonClient client;
    private final long batchSize;
    private final String dbKey;


    public RedisRangeGenerator(@Value("${ranges.batch-size}") long batchSize,
                               RedissonClient client,
                               @Value("${redis.db-key") String dbKey) {
        this.batchSize = batchSize;
        this.client = client;
        this.dbKey = dbKey;
    }

    @Override
    public Range getNextRange() {
        Logger logger = LoggerFactory.getLogger(RangeGenerator.class);
        long maxValue;
        try {
             maxValue = client.getAtomicLong(dbKey).addAndGet(batchSize);
        } catch (RedisException exception) {
            logger.error("{}",exception.getMessage());
            if (exception.getMessage() != null && exception.getMessage().contains("overflow")) {
                throw new RangeOverflowException("Range overflow error");
            }
            else {
                throw new RangeGeneratorException("Error requesting data from Redis " + exception.getMessage());
            }
        }
        logger.info("new id max value = {}, batch size = {}", maxValue, batchSize);
        return new Range(maxValue - batchSize, 1, maxValue);
    }
}
