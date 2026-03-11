package com.nipponhub.nipponhubv0.Controllers;

import com.nipponhub.nipponhubv0.Models.FileDocument;
import com.nipponhub.nipponhubv0.Services.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    // ─── UPLOAD SINGLE ───────────────────────────────────────────────────────────

    /**
     * POST /file/upload
     * Upload a single image. Optionally link it to a product via productId param.
     * Returns the GridFS ObjectId on success.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
        @RequestPart("file") MultipartFile file,
        @RequestParam(required = false) Long productId
    ) {
        try {
            String fileId = fileStorageService.uploadFile(file, productId);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                    "fileId", fileId,
                    "message", "File uploaded successfully"
                ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    // ─── UPLOAD MULTIPLE ─────────────────────────────────────────────────────────

    /**
     * POST /file/upload/batch
     * Upload multiple images at once. Optionally link them all to a product.
     */
    @PostMapping("/upload/batch")
    public ResponseEntity<Map<String, Object>> uploadFiles(
        @RequestPart("files") List<MultipartFile> files,
        @RequestParam(required = false) Long productId
    ) {
        try {
            List<String> fileIds = fileStorageService.uploadFiles(files, productId);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                    "fileIds", fileIds,
                    "count", fileIds.size(),
                    "message", "Files uploaded successfully"
                ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Batch upload failed: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Batch upload failed: " + e.getMessage()));
        }
    }

    // ─── SERVE FILE ──────────────────────────────────────────────────────────────

    /**
     * GET /file/{fileId}
     * Stream an image by its GridFS ObjectId.
     * Sets the correct Content-Type header from stored metadata.
     */
    @GetMapping("/{fileId}")
    public void serveFile(
        @PathVariable String fileId,
        HttpServletResponse response
    ) throws IOException {
        try {
            String contentType = fileStorageService.getContentType(fileId);
            response.setContentType(contentType);

            try (InputStream stream = fileStorageService.getFileById(fileId)) {
                StreamUtils.copy(stream, response.getOutputStream());
            }

        } catch (IllegalArgumentException e) {
            // Thrown if fileId is not a valid ObjectId hex string
            log.warn("Invalid fileId format: {}", fileId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            log.warn("File not found: {}", fileId);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ─── GET METADATA ────────────────────────────────────────────────────────────

    /**
     * GET /file/metadata/{fileId}
     * Returns the FileDocument metadata for a given GridFS id.
     */
    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<?> getFileMetadata(@PathVariable String fileId) {
        return fileStorageService.getFilesByProductId(null)
            .stream()
            .filter(f -> f.getGridFsId().equals(fileId))
            .findFirst()
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Metadata not found for fileId: " + fileId)));
    }

    /**
     * GET /file/product/{productId}
     * Returns all file metadata documents for a given product.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<FileDocument>> getFilesByProduct(
        @PathVariable Long productId
    ) {
        List<FileDocument> files = fileStorageService.getFilesByProductId(productId);
        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(files);
        }
        return ResponseEntity.ok(files);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────────

    /**
     * DELETE /file/{fileId}
     * Delete a file from GridFS and remove its metadata document.
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String fileId) {
        try {
            fileStorageService.deleteFile(fileId);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Invalid fileId: " + fileId));
        } catch (Exception e) {
            log.error("Failed to delete file {}: {}", fileId, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Delete failed: " + e.getMessage()));
        }
    }
}
