package kup.get.service;

import kup.get.config.ZipConfig;
import kup.get.entity.energy.FileOfProgram;
import kup.get.entity.energy.Version;
import kup.get.repository.energy.VersionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VersionService {
    private final VersionRepository repository;
    private final ZipConfig zipConfig;

    public Version save(String name, String info) {
        return repository.save(new Version(name, info));
    }

    public List<FileOfProgram> getNewFiles(Version version) {
        System.out.println("\n\tversion:\t"+version);
        System.out.println("\n\tlistNewFiles: ");
        for(FileOfProgram file: zipConfig.getZipEntry().stream().filter(file -> file.getComment().compareTo(String.valueOf(version.getId())) > 0).collect(Collectors.toList())){
            System.out.println(file.getName() + "\t" +file.getComment() + "\tcontent: " + (file.getContent()!=null));
        }
        return zipConfig.getZipEntry().stream().filter(file -> file.getComment().compareTo(String.valueOf(version.getId())) > 0).collect(Collectors.toList());
    }
    public List<FileOfProgram> whichFilesShouldBeSaved(){
        System.out.println("whichFilesShouldBeSaved");
        return zipConfig.getZipEntry().stream().map(f -> new FileOfProgram(f.getName(), f.getSize(), f.getTime())).collect(Collectors.toList());
    }

    public List<Version> getInformationAboutUpdate(Version version) {
        System.out.println("getInformationAboutUpdate\t"+version.getId());
        return repository.findAllByIdAfter(version.getId());
    }

    public Version getActualVersion() {
        System.out.println("getActualVersion");
        return repository.findFirstByOrderByIdDesc();
    }
}
