import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class GitTester {
     public static void main (String[] args){
        String fileSeperator = File.separator;
        Path testPath = Paths.get("."+fileSeperator+"test");
        Git repo = new Git(false, testPath);
        Path gitPath = Paths.get("git");
        

        if (Files.exists(gitPath)) repo.removeDir(gitPath);
        repo.initRepo();
        repo.initRepo();
        
        try{
            repo.stage(testPath, testPath);
            //repo.createTree(testPath, testPath);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        repo.commit( fileSeperator, fileSeperator);


        try{
            Path testFilePathToBeReset = Files.write(Paths.get("test"+fileSeperator+"ballw.txt"), "basketball".getBytes(StandardCharsets.UTF_8));
            repo.stage(testPath, testPath);

            repo.commit( fileSeperator, fileSeperator);

            testFilePathToBeReset.toFile().delete(); //to test repeatedly
        }
        catch (IOException e){
           e.printStackTrace();
        }

        testPath = Paths.get("."+fileSeperator+"newTest");

        if (Files.exists(testPath)) repo.removeDir(testPath);

        repo.setWorkingDirectory(testPath);

        repo.checkout(repo.latestCommitHash);
        
    }
}
