import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Git{
    public boolean isZip = false;
    public Git(boolean isZip){
        this.isZip = isZip;
    }

    //initialize repository by creating git folder, objects sub folder, and index file
    public void initRepo(){
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

    //resets the entire git folder path by deleting files recursively
    public void resetInit(Path path){
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //gets the name of the blob of the file in variable path by hashing the contents
    public String getBlobName(Path path){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] written = Files.readAllBytes(path);

            if (isZip){
                written = compress(written);
            }

            byte[] messageDigest = md.digest( written);

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            
            return hashtext;

        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return "";
        }
    }

    //creates the blob of the file in variable path by creating a file with the correct blob name and the same contents as the original
    public void makeBlob(Path path){
        String name = getBlobName(path);
        try{
            byte[] written = Files.readAllBytes(path);
            
            if (isZip){
                written = compress(written);
            }
            Files.write(Paths.get("git/objects/"+name), written);
            updateIndex(path);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //updates the index file by adding a new line with entry from blob and file name at variable path 
    private void updateIndex(Path path){
        String name = getBlobName(path);
        String appendString = name + " " + path.getFileName() + "\n";
        try{
            Files.write(Paths.get("git/index"), appendString.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //zip compresses an array of bytes, usually from files
    public static byte[] compress(byte[] in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflaterOutputStream defl = new DeflaterOutputStream(out);
            defl.write(in);
            defl.flush();
            defl.close();

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}