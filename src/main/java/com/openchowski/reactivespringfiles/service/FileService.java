package com.openchowski.reactivespringfiles.service;

import com.openchowski.reactivespringfiles.entity.File;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FileService {
    Flux<File> findAll();
    Mono<File> findById(Long id);
    Flux<File> save(List<FilePart> fileParts);
    Mono<File> update(Long id, File file);
    Mono<Void> delete(Long id);
}
