import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitTesterSky {
    public static void main(String[] args) throws IOException {
        Git repo = new Git(true);
        String fileSeperator = File.separator;

        Path testPath = Paths.get("/Users/skystubbeman/Desktop/tester_folder");
        Path fileTestPath = Paths.get("/Users/skystubbeman/Desktop/tester_folder/test2.txt");

        repo.createTree(testPath, testPath);
    }
}
