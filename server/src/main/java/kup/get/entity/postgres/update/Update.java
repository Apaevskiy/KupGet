package kup.get.entity.postgres.update;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@Data
@NoArgsConstructor
@ToString
public class Update {
    private List<Version> information = new ArrayList<>();
    private List<FileOfProgram> deleteFiles = new ArrayList<>();
}
