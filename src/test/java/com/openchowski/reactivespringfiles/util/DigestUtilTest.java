package com.openchowski.reactivespringfiles.util;

import com.openchowski.reactivespringfiles.enums.AlgorithmEnum;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DigestUtilTest {

    private static final String EXPECTED_DIGEST = "916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9";

    @Test
    void calculateDigest_shouldReturnCorrectDigest() throws Exception {
        String testData = "test data";
        AtomicLong fileSize = new AtomicLong(0);

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(testData.getBytes());
        Flux<DataBuffer> dataBufferFlux = Flux.just(dataBuffer);

        MessageDigest messageDigest = MessageDigest.getInstance(AlgorithmEnum.SHA256.getName());

        Mono<String> result = DigestUtil.calculateDigest(dataBufferFlux, messageDigest, fileSize);

        StepVerifier.create(result)
                .expectNextMatches(digest -> digest.equals(EXPECTED_DIGEST))
                .verifyComplete();

        assertEquals(testData.length(), fileSize.get());
    }

    @Test
    void calculateDigest_shouldHandleError() {
        String testData = "test data";
        AtomicLong fileSize = new AtomicLong(0);

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(testData.getBytes());
        Flux<DataBuffer> dataBufferFlux = Flux.just(dataBuffer);

        MessageDigest messageDigest = mock(MessageDigest.class);

        doThrow(new RuntimeException("Error processing file data"))
                .when(messageDigest)
                .update(any(byte[].class), anyInt(), anyInt());

        Mono<String> result = DigestUtil.calculateDigest(dataBufferFlux, messageDigest, fileSize);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                                && throwable.getMessage().contains("Error processing file data"))
                .verify();
    }
}
