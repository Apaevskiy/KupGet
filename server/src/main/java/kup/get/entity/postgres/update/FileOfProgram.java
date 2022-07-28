package kup.get.entity.postgres.update;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.zip.ZipEntry;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class FileOfProgram implements Serializable {
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