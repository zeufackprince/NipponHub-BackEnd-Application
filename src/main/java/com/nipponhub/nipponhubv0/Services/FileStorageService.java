package com.nipponhub.nipponhubv0.Services;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    /**
     * Upload a single image to MongoDB GridFS.
     * Returns the MongoDB ObjectId (String) of the stored file.
     */
    public String uploadFile(MultipartFile file) throws IOException {

        // Validate the file is an image
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed. Received: " + contentType);
        }

        // Attach metadata so we can query by original name or product later
        DBObject metadata = new BasicDBObject();
        metadata.put("originalName", file.getOriginalFilename());
        metadata.put("contentType", contentType);
        metadata.put("size", file.getSize());

        // Store in GridFS — returns the MongoDB ObjectId
        ObjectId fileId = gridFsTemplate.store(
            file.getInputStream(),
            file.getOriginalFilename(),
            contentType,
            metadata
        );

        log.info("File uploaded to MongoDB GridFS — id: {}, name: {}", fileId, file.getOriginalFilename());

        // Return the id as a String (this is what we store in product.prodUrl)
        return fileId.toString();
    }

    /**
     * Upload multiple images at once.
     * Returns a list of MongoDB ObjectIds (Strings).
     */
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> fileIds = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                fileIds.add(uploadFile(file));
            }
        }
        return fileIds;
    }

    /**
     * Retrieve a file's InputStream from GridFS by its ObjectId.
     * Used by the controller to serve the image.
     */
    public InputStream getFileById(String fileId) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
            new Query(Criteria.where("_id").is(fileId))
        );

        if (gridFSFile == null) {
            throw new RuntimeException("File not found in MongoDB: " + fileId);
        }

        return gridFsOperations.getResource(gridFSFile).getInputStream();
    }

    /**
     * Get the content type of a stored file (e.g. "image/png").
     */
    public String getContentType(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
            new Query(Criteria.where("_id").is(fileId))
        );

        if (gridFSFile == null || gridFSFile.getMetadata() == null) {
            return "image/jpeg"; // safe default
        }

        return gridFSFile.getMetadata().getString("contentType");
    }

    /**
     * Delete a single file from GridFS by its ObjectId.
     */
    public void deleteFile(String fileId) {
        gridFsTemplate.delete(
            new Query(Criteria.where("_id").is(fileId))
        );
        log.info("File deleted from MongoDB GridFS — id: {}", fileId);
    }

    /**
     * Delete multiple files at once (used when updating a product's images).
     */
    public void deleteFiles(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) return;
        for (String fileId : fileIds) {
            deleteFile(fileId);
        }
    }
}
