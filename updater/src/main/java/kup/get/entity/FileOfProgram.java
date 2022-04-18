package kup.get.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

@Data
@NoArgsConstructor
@JsonIgnoreProperties({"file"})
public class FileOfProgram {
    private String name;
    private Long size;
    private Long time;
    private byte[] content;

    public FileOfProgram(ZipEntry zipEntry, byte[] content) {
        this.name = zipEntry.getName().replaceAll("/","\\\\");
        this.size = zipEntry.getSize();
        this.time = zipEntry.getTime();
        this.content = content;
    }

    public FileOfProgram(JarEntry zipEntry) {
        this.name = zipEntry.getName().replaceAll("/","\\\\");
        this.size = zipEntry.getSize();
        this.time = zipEntry.getTime();
    }



    @Override
    public int hashCode() {
        return Objects.hash(name, size, time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileOfProgram that = (FileOfProgram) o;
        return Objects.equals(name, that.name) && Objects.equals(size, that.size) && Objects.equals(time, that.time);
    }
}
