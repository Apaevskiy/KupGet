package kup.get.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Data
@NoArgsConstructor
@ToString
public class Update {
    private List<Version> information = new ArrayList<>();
    private List<FileOfProgram> deleteFiles = new ArrayList<>();
}