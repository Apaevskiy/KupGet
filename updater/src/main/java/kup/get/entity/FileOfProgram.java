package kup.get.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class FileOfProgram {
    private String name;
    private long size;
    private long time;
    private long compressedSize;
    private int method;
    private boolean downloaded = false;
}
