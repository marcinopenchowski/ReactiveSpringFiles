package com.openchowski.reactivespringfiles.service;

import com.openchowski.reactivespringfiles.enums.AlgorithmEnum;
import com.openchowski.reactivespringfiles.entity.File;
import com.openchowski.reactivespringfiles.repo.FileRepo;
import com.openchowski.reactivespringfiles.storage.service.StorageService;
import com.openchowski.reactivespringfiles.util.DigestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final long INIT_VALUE = 0;

    private final FileRepo fileRepo;
    private final StorageService storageService;

    @Override
    public Flux<File> findAll() {
        return fileRepo.findAll();
    }

    @Override
    public Mono<File> findById(Long id) {
        return fileRepo.findById(id);
    }

    @Override
    public Flux<File> save(List<FilePart> fileParts) {
        return Flux.fromIterable(fileParts)
                .flatMap(filePart -> {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(AlgorithmEnum.SHA256.getName());
                        Flux<DataBuffer> dataBufferFlux = filePart.content();
                        final AtomicLong fileSize = new AtomicLong(INIT_VALUE);

                        return storeFile(filePart.filename(), dataBufferFlux)
                                .then(DigestUtil.calculateDigest(dataBufferFlux, messageDigest, fileSize))
                                .map(digest ->
                                        File.builder()
                                                .name(filePart.filename())
                                                .digest(digest)
                                                .size(fileSize.get())
                                                .build());
                    } catch (NoSuchAlgorithmException e) {
                        return Mono.error(new RuntimeException("Error initializing MessageDigest", e));
                    }
                })
                .flatMap(fileRepo::save);
    }

    @Override
    public Mono<File> update(Long id, File file) {
        return fileRepo.findById(id)
                .flatMap(fileToUpdate -> {
                    fileToUpdate.setDigest(file.getDigest());
                    fileToUpdate.setName(file.getName());
                    fileToUpdate.setSize(file.getSize());
                    return fileRepo.save(fileToUpdate);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return fileRepo.deleteById(id);
    }

    private Mono<Void> storeFile(String filename, Flux<DataBuffer> dataBufferFlux) {
        return dataBufferFlux.doOnNext(dataBuffer -> {
                    try (InputStream inputStream = dataBuffer.asInputStream()) {
                        storageService.store(filename, inputStream);
                    } catch (IOException e) {
                        throw new RuntimeException("Error storing file", e);
                    }
                })
                .then();
    }
}
