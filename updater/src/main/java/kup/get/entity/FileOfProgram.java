package kup.get.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.zip.ZipEntry;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class FileOfProgram {
    private String name;
    private long size;
    private long time;
    private long compressedSize;
    private int method;

    public FileOfProgram(ZipEntry zipEntry) {
        this.name = zipEntry.getName();
        this.size = zipEntry.getSize();
        this.time = zipEntry.getTime();
        this.compressedSize = zipEntry.getCompressedSize();
        this.method = zipEntry.getMethod();
    }

}
