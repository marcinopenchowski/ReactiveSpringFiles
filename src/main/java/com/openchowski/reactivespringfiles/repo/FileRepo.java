package com.openchowski.reactivespringfiles.repo;

import com.openchowski.reactivespringfiles.entity.File;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends ReactiveCrudRepository<File, Long> {
}
