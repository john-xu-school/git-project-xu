import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;
import java.nio.charset.StandardCharsets;

public class Git{
    public static void main (String[] args){
        Path path = Paths.get("git");
        resetInit(path);
        initRepo();
        initRepo();
    }

    public static void initRepo(){
        
        try{
            Path pathGit = Paths.get("git");
            Path pathObjects = Paths.get("git/objects");
            if(Files.exists(pathGit)){
                System.out.println ("Git Repository already exists");
                return;
            }
            Files.createDirectories(pathGit);
            Files.createDirectories(pathObjects);
            Files.write(Paths.get("git/index"), "".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void resetInit(Path path){
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .peek(System.out::println)
            .forEach(File::delete);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static String getBlobName(Path path){
        try{
            return "" + Files.readAllBytes(path).hashCode();

        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public static void makeBlob(Path path){
        String name = getBlobName(path);
        try{
            Files.write(Paths.get("git/"+name), Files.readAllBytes(path));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}