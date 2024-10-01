import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.DeflaterOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.ArrayList;

public class Git{
    public boolean isZip = false;
    public boolean includeHidden = true;
    public String fileSeperator = "";
    public Git(boolean isZip){
        this.isZip = isZip;
        fileSeperator = File.separator;
    }

    //initialize repository by creating git folder, objects sub folder, and index file
    public void initRepo(){
        try{
            Path pathGit = Paths.get("git");
            Path pathObjects = Paths.get("git"+fileSeperator+"objects");
            if(Files.exists(pathGit)){
                System.out.println ("Git Repository already exists");
                return;
            }
            Files.createDirectories(pathGit);
            Files.createDirectories(pathObjects);
            Files.write(Paths.get("git"+fileSeperator+"index"), "".getBytes(StandardCharsets.UTF_8));
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

            byte[] messageDigest = md.digest(written);
           
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
    public void makeBlob(Path filePath, Path root){
        String name = getBlobName(filePath);
        try{
            byte[] written = Files.readAllBytes(filePath);
            
            if (isZip){
                written = compress(written);
            }
    
            Files.write(Paths.get("git"+fileSeperator+"objects"+fileSeperator+name), written);
            updateIndex(filePath, root, filePath, "blob");   

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //updates the index file by adding a new line with entry from blob and file name at variable path 
    private void updateIndex(Path path, Path root, Path filePath, String type) throws IOException{
        String appendString = "";
        String name = getBlobName(path);
        File file = new File ("git"+fileSeperator+"index");
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);

        appendString = type + " " + name + " " + root.relativize(filePath).toString() + "\n";
        bw.write(appendString);
    
        bw.close();
    }

   //creates a tree blob and blobs of all the files inside it, updates everything to index
    public String createTree(Path directoryPath, Path root) throws IOException {
        if (!Files.exists(directoryPath)) {
            throw new FileNotFoundException("the directory path '" + directoryPath.toString() + "' doesn't exist!");
        }

        if (!Files.isDirectory(directoryPath)) {
            throw new NotDirectoryException("the path '" + directoryPath.toString() + " isn't a directory!");
        }

        ArrayList<String> treeEntries = new ArrayList<>();

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)){
            Iterator<Path> iterator = stream.iterator();

            while (iterator.hasNext()){ 
                Path entry = iterator.next();

                if (!includeHidden && entry.getFileName().toString().startsWith(".")) {
                    continue;
                }
                
                try{
                    if(Files.isDirectory(entry)){
                        String subTreeHash = createTree(entry, root);
                        String treeEntry = "tree " + subTreeHash + " " + directoryPath.relativize(entry).toString();
                        treeEntries.add(treeEntry);
            
                    } else if (Files.isRegularFile(entry)){
                        makeBlob(entry, root.getParent());
                        String blobHash = getBlobName(entry);
                        String blobEntry = "blob " + blobHash + " " + directoryPath.relativize(entry).toString();
                        treeEntries.add(blobEntry);
                    }
                } catch (AccessDeniedException e){
                    System.out.println("permission denied for: " + entry.toString());
                }
            }
        } catch (AccessDeniedException e){
            System.out.println("permission denied for directory: " + directoryPath.toString());
        }
        StringBuilder tree = new StringBuilder();

        File tempFile = new File("./", "tempFile");

        if (!treeEntries.isEmpty()){
            FileWriter fw = new FileWriter(tempFile);
            for (String entry : treeEntries){
                tree.append(entry);
                tree.append("\n");
            }
            fw.write(tree.toString());
            fw.close();
        }

        String hashText = getBlobName(Paths.get(tempFile.getPath()));
        File realTree = new File("git"+fileSeperator+"objects", hashText);
        tempFile.renameTo(realTree);
        updateIndex(Paths.get(realTree.getPath()), root.getParent(), directoryPath, "tree");
        
        return hashText;
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