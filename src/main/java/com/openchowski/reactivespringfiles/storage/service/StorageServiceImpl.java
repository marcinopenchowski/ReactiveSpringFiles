package com.openchowski.reactivespringfiles.storage.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class StorageServiceImpl implements StorageService {

    @Override
    public void store(String fileName, InputStream content) {
        // empty implementation
    }
}
