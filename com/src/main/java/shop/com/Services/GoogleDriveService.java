package shop.com.Services;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleDriveService {

    @Autowired
    private Drive googleDrive;

    @Value("${google.drive.folder.id}")
    private String googleDriveFolderId;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        // Convert MultipartFile to a Google Drive File
        File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(googleDriveFolderId));

        java.io.File filePath = new java.io.File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(filePath);

        FileContent mediaContent = new FileContent(multipartFile.getContentType(), filePath);
        File uploadedFile = googleDrive.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploadedFile.getWebViewLink();
    }
}
