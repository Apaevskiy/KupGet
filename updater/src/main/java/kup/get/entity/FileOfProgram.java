package kup.get.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.zip.ZipEntry;

@Data
@NoArgsConstructor
public class FileOfProgram {
    private String name;
    private long size;
    private long time;

    public FileOfProgram(ZipEntry zipEntry) {
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
