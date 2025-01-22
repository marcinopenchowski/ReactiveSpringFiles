package com.openchowski.reactivespringfiles.storage.service;

import java.io.InputStream;

public interface StorageService {

    void store(String fileName, InputStream content);
}
