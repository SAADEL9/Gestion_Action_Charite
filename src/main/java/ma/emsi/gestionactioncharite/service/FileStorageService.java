package ma.emsi.gestionactioncharite.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String store(MultipartFile file, String subDir) throws IOException {
        Path path = root.resolve(subDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), path.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/" + subDir + "/" + filename;
    }

    public void delete(String filePath) {
        try {
            if (filePath == null || !filePath.startsWith("/uploads/")) return;
            
            // Remove leading "/" for path resolution if necessary, or resolve relative to root
            String relativePath = filePath.substring(1); 
            Path path = Paths.get(relativePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't fail
            System.err.println("Could not delete file: " + filePath + ". Error: " + e.getMessage());
        }
    }
}
