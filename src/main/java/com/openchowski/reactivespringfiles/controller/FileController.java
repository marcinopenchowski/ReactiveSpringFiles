package com.openchowski.reactivespringfiles.controller;

import com.openchowski.reactivespringfiles.entity.File;
import com.openchowski.reactivespringfiles.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping()
    public Flux<File> findAll() {
        return fileService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<File> findById(@PathVariable Long id) {
        return fileService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<File> save(@RequestPart("file") List<FilePart> filePart) {
        return fileService.save(filePart);
    }

    @PutMapping("/{id}")
    public Mono<File> update(@PathVariable Long id, @RequestBody File file) {
        return fileService.update(id, file);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return fileService.delete(id);
    }
}
