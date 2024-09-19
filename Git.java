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
            Path path = Paths.get("git");
            if(Files.exists(path)){
                System.out.println ("Git Repository already exists");
                return;
            }
            Files.createDirectories(path);
            Files.write(Paths.get("git/objects"), "".getBytes(StandardCharsets.UTF_8));
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
}