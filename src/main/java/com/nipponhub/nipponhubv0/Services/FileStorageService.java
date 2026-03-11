package com.nipponhub.nipponhubv0.Services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.nipponhub.nipponhubv0.Models.FileDocument;
import com.nipponhub.nipponhubv0.Repositories.mongodb.FileRepository;
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
    private final FileRepository fileRepository;

    // ─── UPLOAD ─────────────────────────────────────────────────────────────────

    /**
     * Upload a single image to MongoDB GridFS and save its metadata.
     *
     * @param file the multipart image file
     * @return the GridFS ObjectId as a String
     * @throws IOException              if reading the file stream fails
     * @throws IllegalArgumentException if the file is not an image
     */
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, null);
    }

    /**
     * Upload a single image linked to a specific product.
     *
     * @param file      the multipart image file
     * @param productId optional MySQL product id to associate with this file
     * @return the GridFS ObjectId as a String
     */
    public String uploadFile(MultipartFile file, Long productId) throws IOException {

        // ── Validate ──────────────────────────────────────────────────────────
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                "Only image files are allowed. Received: " + contentType
            );
        }

        // ── Build GridFS metadata ─────────────────────────────────────────────
        DBObject metadata = new BasicDBObject();
        metadata.put("originalName", file.getOriginalFilename());
        metadata.put("contentType", contentType);
        metadata.put("size", file.getSize());
        if (productId != null) {
            metadata.put("productId", productId);
        }

        // ── Store in GridFS ───────────────────────────────────────────────────
        ObjectId fileId = gridFsTemplate.store(
            file.getInputStream(),
            file.getOriginalFilename(),
            contentType,
            metadata
        );

        // ── Persist metadata document ─────────────────────────────────────────
        FileDocument doc = FileDocument.builder()
            .gridFsId(fileId.toString())
            .originalName(file.getOriginalFilename())
            .contentType(contentType)
            .size(file.getSize())
            .productId(productId)
            .build();

        fileRepository.save(doc);

        log.info("File uploaded — gridFsId: {}, name: {}, productId: {}",
            fileId, file.getOriginalFilename(), productId);

        return fileId.toString();
    }

    /**
     * Upload multiple images at once (no product association).
     */
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        return uploadFiles(files, null);
    }

    /**
     * Upload multiple images, all linked to the same product.
     */
    public List<String> uploadFiles(List<MultipartFile> files, Long productId) throws IOException {
        List<String> fileIds = new ArrayList<>();
        if (files == null || files.isEmpty()) return fileIds;

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                fileIds.add(uploadFile(file, productId));
            }
        }
        return fileIds;
    }

    // ─── RETRIEVE ────────────────────────────────────────────────────────────────

    /**
     * Retrieve a file's InputStream from GridFS by its ObjectId string.
     *
     * @throws IOException if the file is not found or stream can't be opened
     */
    public InputStream getFileById(String fileId) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
            new Query(Criteria.where("_id").is(new ObjectId(fileId))) // ✅ ObjectId — not raw String
        );

        if (gridFSFile == null) {
            throw new IOException("File not found in GridFS: " + fileId);
        }

        return gridFsOperations.getResource(gridFSFile).getInputStream();
    }

    /**
     * Get the MIME content type of a stored file.
     * Falls back to "image/jpeg" if metadata is missing.
     *
     * FIX: was using raw String for _id — now correctly uses ObjectId.
     */
    public String getContentType(String fileId) {

        // ── Try metadata repo first (faster) ─────────────────────────────────
        return fileRepository.findByGridFsId(fileId)
            .map(FileDocument::getContentType)
            .orElseGet(() -> {
                // ── Fallback: query GridFS directly ──────────────────────────
                GridFSFile gridFSFile = gridFsTemplate.findOne(
                    new Query(Criteria.where("_id").is(new ObjectId(fileId))) // ✅ Fixed
                );

                if (gridFSFile == null || gridFSFile.getMetadata() == null) {
                    log.warn("Content type not found for fileId: {} — defaulting to image/jpeg", fileId);
                    return "image/jpeg";
                }

                return gridFSFile.getMetadata().getString("contentType");
            });
    }

    /**
     * Get all file metadata documents associated with a product.
     */
    public List<FileDocument> getFilesByProductId(Long productId) {
        return fileRepository.findAllByProductId(productId);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────────

    /**
     * Delete a single file from GridFS and remove its metadata document.
     *
     * FIX: was using raw String for _id — now correctly uses ObjectId.
     */
    public void deleteFile(String fileId) {
        // ── Delete from GridFS ────────────────────────────────────────────────
        gridFsTemplate.delete(
            new Query(Criteria.where("_id").is(new ObjectId(fileId))) // ✅ Fixed
        );

        // ── Delete metadata document ──────────────────────────────────────────
        fileRepository.deleteByGridFsId(fileId);

        log.info("File deleted — gridFsId: {}", fileId);
    }

    /**
     * Delete multiple files at once.
     */
    public void deleteFiles(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) return;
        for (String fileId : fileIds) {
            deleteFile(fileId);
        }
    }
}
