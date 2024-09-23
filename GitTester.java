import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitTester {
     public static void main (String[] args){
        Git repo = new Git(true);
        String fileSeperator = File.separator;
        Path gitPath = Paths.get("git");

        if (Files.exists(gitPath)) repo.resetInit(gitPath);
        repo.initRepo();
        repo.initRepo();

        

        Path fileToBlobPath = Paths.get("README.md");
        Path fileToBlobPath2 = Paths.get(".gitignore");

        //reseting objects
        Path blobPath = Paths.get("git"+fileSeperator+"objects/" + repo.getBlobName(fileToBlobPath));
        Path blobPath2 = Paths.get("git"+fileSeperator+"objects"+fileSeperator+ repo.getBlobName(fileToBlobPath2));
        if (Files.exists(blobPath )) {
            try{
                Files.delete(blobPath);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        if(Files.exists(blobPath2)){
            try{
                Files.delete(blobPath2);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        //resetting index
        try{
            Files.write(Paths.get("git"+fileSeperator+"index"), "".getBytes());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        //recreating blobs
        repo.makeBlob(fileToBlobPath);
        repo.makeBlob(fileToBlobPath2);

        //testing blob name
        File blob1 = new File(blobPath.toString());
        File blob2 = new File(blobPath2.toString());

        if (blob1.getName().equals(repo.getBlobName(fileToBlobPath))){
            System.out.println("Blob 1 name correct");
        }
        else{
            System.out.println("Blob 2 name INCORRECT");
        }

        if (blob2.getName().equals(repo.getBlobName(fileToBlobPath2))){
            System.out.println("Blob 2 name correct");
        }
        else{
            System.out.println("Blob 2 name INCORRECT");
        }

        //Check index content
        String expectedIndexContents = repo.getBlobName(fileToBlobPath) + " " + fileToBlobPath.getFileName() 
        + "\n" + repo.getBlobName(fileToBlobPath2) + " " + 
        fileToBlobPath2.getFileName() + "\n";

        String indexContents = "";
        try{
            indexContents = Files.readString(Paths.get("git"+fileSeperator+"index"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        

        if (expectedIndexContents.equals(indexContents)){
            System.out.println("Index Updated Correctly");
        }
        else{
            System.out.println("Index Updated INCORRECTLY");
        }

    }
}
