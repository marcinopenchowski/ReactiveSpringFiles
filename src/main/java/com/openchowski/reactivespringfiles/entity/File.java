package com.openchowski.reactivespringfiles.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("files")
public class File {

    @Id
    private Long id;
    private String name;
    private String digest;
    private Long size;
}
