package com.openchowski.reactivespringfiles.service;

import com.openchowski.reactivespringfiles.entity.File;
import com.openchowski.reactivespringfiles.repo.FileRepo;
import com.openchowski.reactivespringfiles.storage.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class FileServiceImplTest {

    @Mock
    private FileRepo fileRepo;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private FileServiceImpl fileService;

    private File file1;
    private File file2;

    @BeforeEach
    void setUp() {
        file1 = File.builder()
                .id(1L)
                .name("testFile1.txt")
                .size(1L)
                .digest("testDigest1")
                .build();
        file2 = File.builder()
                .id(2L)
                .name("testFile2.txt")
                .size(2L)
                .digest("testDigest2")
                .build();
    }

    @Test
    void findAll_shouldReturnAllFiles() {
        when(fileRepo.findAll()).thenReturn(Flux.just(file1, file2));

        Flux<File> result = fileService.findAll();

        StepVerifier.create(result)
                .expectNext(file1, file2)
                .verifyComplete();

        verify(fileRepo, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnFileIfExists() {
        when(fileRepo.findById(file1.getId())).thenReturn(Mono.just(file1));

        Mono<File> result = fileService.findById(file1.getId());

        StepVerifier.create(result)
                .expectNext(file1)
                .verifyComplete();

        verify(fileRepo, times(1)).findById(file1.getId());
    }

    @Test
    void save_shouldStoreAndSaveFiles() {
        FilePart filePart = mock(FilePart.class);
        DataBuffer dataBuffer = mock(DataBuffer.class);
        InputStream inputStream = new ByteArrayInputStream("mockData".getBytes(StandardCharsets.UTF_8));
        Flux<DataBuffer> dataBufferFlux = Flux.just(dataBuffer);

        when(dataBuffer.asInputStream()).thenReturn(inputStream);
        when(filePart.filename()).thenReturn("testFile1.txt");
        when(filePart.content()).thenReturn(dataBufferFlux);
        when(fileRepo.save(any(File.class))).thenReturn(Mono.just(file1));

        Flux<File> result = fileService.save(List.of(filePart));

        StepVerifier.create(result)
                .expectNext(file1)
                .verifyComplete();

        verify(fileRepo, times(1)).save(any(File.class));
        verify(storageService, times(1)).store(eq("testFile1.txt"), any(InputStream.class));
    }

    @Test
    void update_shouldUpdateAndSaveFile() {
        File updatedFile = File.builder()
                .id(1L)
                .name("updatedFile.txt")
                .size(3L)
                .digest("updatedDigest")
                .build();

        when(fileRepo.findById(file1.getId())).thenReturn(Mono.just(file1));
        when(fileRepo.save(any(File.class))).thenReturn(Mono.just(updatedFile));

        Mono<File> result = fileService.update(file1.getId(), updatedFile);

        StepVerifier.create(result)
                .expectNext(updatedFile)
                .verifyComplete();

        verify(fileRepo, times(1)).findById(file1.getId());
        verify(fileRepo, times(1)).save(any(File.class));
    }

    @Test
    void delete_shouldRemoveFile() {
        when(fileRepo.deleteById(file1.getId())).thenReturn(Mono.empty());

        Mono<Void> result = fileService.delete(file1.getId());

        StepVerifier.create(result)
                .verifyComplete();

        verify(fileRepo, times(1)).deleteById(file1.getId());
    }
}