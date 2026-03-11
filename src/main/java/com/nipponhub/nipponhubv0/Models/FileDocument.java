package com.nipponhub.nipponhubv0.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents metadata for a file stored in MongoDB GridFS.
 * The actual binary content lives in GridFS (fs.files + fs.chunks).
 * This document mirrors/tracks what's in GridFS for querying convenience.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "file_metadata")
public class FileDocument {

    @Id
    private String id;

    /** The GridFS ObjectId returned after storing the file — stored as String */
    private String gridFsId;

    /** Original file name as uploaded by the client */
    private String originalName;

    /** MIME type e.g. "image/png", "image/jpeg" */
    private String contentType;

    /** File size in bytes */
    private Long size;

    /** Timestamp of upload */
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    /** Optional: reference to the product this file belongs to */
    private Long productId;
}
