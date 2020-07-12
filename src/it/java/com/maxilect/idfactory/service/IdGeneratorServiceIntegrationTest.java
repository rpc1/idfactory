
package com.maxilect.idfactory.service;

import com.maxilect.idfactory.exceptions.RangeOverflowException;
import com.maxilect.idfactory.model.Range;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.testcontainers.containers.GenericContainer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class IdGeneratorServiceIntegrationTest {

    @ClassRule
    public static GenericContainer redis =
            new GenericContainer<>("redis")
                    .withExposedPorts(6379);

    private static RedissonClient client;


    @BeforeAll
    public static void init() {
        Config config = new Config();
        redis.start();
        config.useSingleServer()
                .setAddress("redis://" + redis.getContainerIpAddress() + ":" + redis.getFirstMappedPort());
        client = Redisson.create(config);
    }

    @Test
    void generateUniqueId_extremeValues() {
        RangeGenerator redisRangeGenerator = new RedisRangeGenerator(Long.MAX_VALUE - 1, client, "extreme_test");
        Range range = redisRangeGenerator.getNextRange();
        Assertions.assertEquals(Long.MAX_VALUE - 1, range.getMax());

    }

    @Test
    void generateUniqueId_longOverflowShouldThrowException() {
        RangeGenerator redisRangeGenerator = new RedisRangeGenerator(Long.MAX_VALUE - 1, client, "overflow_test");
        redisRangeGenerator.getNextRange();
        Assertions.assertThrows(RangeOverflowException.class, redisRangeGenerator::getNextRange);
    }


    @Test
    void generateUniqueId_shouldReturnUniqueValues() throws InterruptedException {
        int threads = 10;
        int numberOfTries = 1000;
        Set<Long> ids = new HashSet<>();

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.execute(new TestService(ids, numberOfTries));
        }
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);
    }

    public static class TestService implements Runnable {

        private final Set<Long> ids;
        private final int numberOfTries;
        private final IdGeneratorService idGenerator;

        public TestService(Set<Long> ids, int numberOfTries) {
            this.ids = ids;
            this.numberOfTries = numberOfTries;
            RangeGenerator redisRangeGenerator = new RedisRangeGenerator(10, client, "load-test");
            idGenerator = new IdGeneratorService(redisRangeGenerator);
        }

        @Override
        public void run() {
            for (int i = 0; i < numberOfTries; i++) {
                Long uId = idGenerator.generateUniqueId().getId();
                Assertions.assertFalse(ids.contains(uId), "Id value is not unique " + uId);
                ids.add(uId);
            }
        }
    }

}