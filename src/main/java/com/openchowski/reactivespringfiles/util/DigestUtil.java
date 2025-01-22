package com.openchowski.reactivespringfiles.util;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicLong;

public class DigestUtil {
    public static Mono<String> calculateDigest(
            Flux<DataBuffer> dataBufferFlux,
            MessageDigest messageDigest,
            AtomicLong fileSize) {
        return dataBufferFlux
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(dataBuffer -> {
                    fileSize.addAndGet(dataBuffer.readableByteCount());
                    try (InputStream inputStream = dataBuffer.asInputStream()) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            messageDigest.update(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error processing file data", e);
                    }
                })
                .then(Mono.fromCallable(() -> HexFormat.of().formatHex(messageDigest.digest())));
    }
}
