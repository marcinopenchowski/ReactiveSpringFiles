package com.openchowski.reactivespringfiles.controller;

import com.openchowski.reactivespringfiles.entity.File;
import com.openchowski.reactivespringfiles.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@WebFluxTest(FileController.class)
@ExtendWith(SpringExtension.class)
class FileControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    FileService fileService;

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
    void findAll_ShouldFindAllFiles() {
        when(fileService.findAll()).thenReturn(Flux.just(file1, file2));

        webTestClient.get()
                .uri("/files")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(File.class)
                .hasSize(2)
                .contains(file1, file2);

        verify(fileService, times(1)).findAll();
    }

    @Test
    void findById_ShouldFindFileById() {
        when(fileService.findById(file1.getId())).thenReturn(Mono.just(file1));

        webTestClient.get()
                .uri("/files/{id}", file1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(File.class)
                .isEqualTo(file1);

        verify(fileService, times(1)).findById(file1.getId());
    }

    @Test
    void save_ShouldReturnSavedFile() {
        when(fileService.save(anyList())).thenReturn(Flux.just(file1, file2));

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", "file content1").filename("testFile1.txt");
        multipartBodyBuilder.part("file", "file content2").filename("testFile2.txt");

        webTestClient.post()
                .uri("/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(multipartBodyBuilder.build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(File.class)
                .hasSize(2)
                .contains(file1, file2);

        verify(fileService, times(1)).save(anyList());
    }

    @Test
    void update_ShouldReturnUpdatedFile() {
        File updatedFile = File.builder()
                .id(1L)
                .name("testFile2.txt")
                .size(2L)
                .digest("testDigest2")
                .build();
        when(fileService.update(eq(file1.getId()), any(File.class))).thenReturn(Mono.just(updatedFile));

        webTestClient.put()
                .uri("/files/{id}", file1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedFile)
                .exchange()
                .expectStatus().isOk()
                .expectBody(File.class)
                .isEqualTo(updatedFile);

        verify(fileService, times(1)).update(eq(file1.getId()), any(File.class));
    }

    @Test
    void delete_ShouldReturnOk() {
        when(fileService.delete(file1.getId())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/files/{id}", file1.getId())
                .exchange()
                .expectStatus().isOk();

        verify(fileService, times(1)).delete(file1.getId());
    }
}