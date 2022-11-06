package Data;

import Common.TaskType;
import CustomException.UnsupportedTaskType;
import Tasks.Deadline;
import Tasks.Event;
import Tasks.TaskInterface;
import Tasks.Todo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileFactory implements FileInterface {
    private String path;
    private String folder = "duke-data";
    private File file;
    private String defaultFile;

    public FileFactory(String defaultFile) {
        this.defaultFile = defaultFile;
        path = folder +'/' + "file.txt";
        setupFile();
    }

    private void setupFile() {
        try {
            //check if folder exists
            File dir = new File(folder);
            if(dir.exists() == false) {
                dir.mkdir();
            }
            file = new File(path);
            if(file.exists() == false) {
                file.createNewFile();
                addDefaultRecord();
            }
        } catch (IOException e) {

        }
    }

    private void addDefaultRecord() {
        String record = "";
        record += defaultFile + "|" + "1" + "|" + "default";
        record += "\n";
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(record);
            fw.close();
        } catch (Exception e) {

        }
    }

    @Override
    public ArrayList<FileInfo> getAllFile() {
        ArrayList<FileInfo> arr = new ArrayList<>();
        try {
            Scanner s = new Scanner(file); // create a Scanner using the File as the source
            while (s.hasNext()) {
                FileInfo info = convertStringToFileInfo(s.nextLine());
                arr.add(info);
            }
        } catch(Exception e ) {

        }
        return arr;
    }

    @Override
    public void setActive(String alias) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_8);
        int i = 0;
        while(i < lines.size()) {
            FileInfo current = convertStringToFileInfo(lines.get(i));
            if(current.getAlias().equalsIgnoreCase(alias)) {
                current.setActive(true);
                String newString = convertFileInfoToString(current);
                lines.set(i, newString);
            } else {
                current.setActive(false);
                lines.set(i, convertFileInfoToString(current));
            }
            i++;
        }

        Files.write(Path.of(file.getAbsolutePath()), lines, StandardCharsets.UTF_8);
    }

    @Override
    public FileInfo getActiveFile() throws IOException, IndexOutOfBoundsException {
        List<String> lines = Files.readAllLines(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_8);
        int i = 0;
        while(i < lines.size()) {
            FileInfo current = convertStringToFileInfo(lines.get(i));
            if(current.isActive()) {
                return current;
            }
            i++;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean addNewFile(String alias) throws IOException {
        ArrayList<FileInfo> allFiles = getAllFile();
        for(FileInfo each : allFiles) {
            if(each.getAlias() == alias) {
                return false;
            }
        }
        FileInfo newFile = new FileInfo("dukefile" + allFiles.size() + ".txt", true, alias);
        String text = convertFileInfoToString(newFile);
        text += "\n";
        FileWriter fw = new FileWriter(file, true);
        fw.write(text);
        fw.close();
        setActive(alias);
        return true;
    }

    private String convertFileInfoToString(FileInfo file) {
        String record = "";
        record += file.getPath() + (file.isActive() == true ? "|1|" : "|0|") + file.getAlias();
        return record;
    }

    private FileInfo convertStringToFileInfo(String text) {
        String[] texts = text.split("\\|");
        if(texts.length == 0) {
            return null;
        }
        return new FileInfo(texts[0], Integer.parseInt(texts[1]) == 1, texts[2]);
    }
}
