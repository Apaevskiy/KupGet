package kup.get.service;

import javafx.concurrent.Task;
import kup.get.controller.socket.SocketController;
import kup.get.model.FileOfProgram;
import kup.get.model.Version;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class UpdateTask extends Task<File> {
    private final SocketController socketController;
    private final ZipService zipService;
    private final QueryTask queryTask;

    private final Version version;
    private final Thread threadQueryWriter;

    public UpdateTask(SocketController socketController, ZipService zipService, QueryTask queryTask, Version version, Thread threadQueryWriter) {
        this.socketController = socketController;
        this.zipService = zipService;
        this.queryTask = queryTask;
        this.version = version;
        this.threadQueryWriter = threadQueryWriter;

    }

    private void copy(String message) {
        queryTask.put(message);
    }

    @Override
    protected File call() throws IOException {
        threadQueryWriter.start();

        this.copy("Загрузка обновлений");
        List<FileOfProgram> listNewFiles = socketController.getUpdateFiles(version);

        File programFile = getProgramFile();
        this.copy("Проверка данных");
        List<FileOfProgram> oldFiles = zipService.readFile(programFile);
        if (listNewFiles.size() != 0) {
            this.copy("Удаление ненужых файлов");
            List<FileOfProgram> listSavedFiles = socketController.getSavedFiles();

            oldFiles.retainAll(listSavedFiles);

            this.copy("Устанавка обновлений");
            oldFiles.addAll(listNewFiles);

            for (FileOfProgram file : oldFiles)
                System.out.println(file.getName());

            Files.deleteIfExists(programFile.toPath());
            zipService.writeZip(oldFiles, new File(programFile.getAbsolutePath()));
            this.copy("Обновление успешно установлено");
            this.copy("stop");
            if (threadQueryWriter.isAlive()) {
                try {
                    threadQueryWriter.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return programFile;
        }
        return null;
    }

    private File getProgramFile() {
        File programFile = null;
        URL resource = this.getClass().getResource("/program/program.jar");
        if (resource != null) {
            programFile = new File(resource.getFile());
        } else {
            resource = this.getClass().getResource("/program");
            if (resource != null) programFile = new File(resource.getPath() + "/program.jar");
            else System.exit(0);
            try {
                Files.createFile(programFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return programFile;
    }
}
