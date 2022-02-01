package kup.get.entity.energy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;

@Data
@NoArgsConstructor
@JsonIgnoreProperties({"file"})
public class FileOfProgram {
    private String name;
    private Long size;
    private Long time;
    private transient ZipEntry file;
    private byte[] content;
    private String comment;

    public FileOfProgram(ZipEntry zipEntry, byte[] content) {
        this.name = zipEntry.getName();
        this.size = zipEntry.getSize();
        this.time = zipEntry.getTime();
        if (zipEntry.getExtra() != null)
            this.comment = new String(zipEntry.getExtra());
        this.file = zipEntry;
        this.content = content;
    }

    public FileOfProgram(ZipEntry zipEntry) {
        this.name = zipEntry.getName();
        this.size = zipEntry.getSize();
        this.time = zipEntry.getTime();
        if (zipEntry.getExtra() != null)
            this.comment = new String(zipEntry.getExtra());
        this.file = zipEntry;
    }

    public FileOfProgram(String name, Long size, Long time) {
        this.name = name;
        this.size = size;
        this.time = time;
    }

    public void setComment(Long comment) {
        this.comment = String.valueOf(comment);
        if (file != null)
            this.file.setExtra(this.comment.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, time, file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileOfProgram that = (FileOfProgram) o;
        return (file != null && file.isDirectory() && that.file != null && that.file.isDirectory())
                ? Objects.equals(name, that.name)
                : Objects.equals(name, that.name) && Objects.equals(size, that.size) && Objects.equals(time, that.time);
    }
}