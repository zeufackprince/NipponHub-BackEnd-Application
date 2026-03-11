package com.nipponhub.nipponhubv0.Repositories.mongodb;

import com.nipponhub.nipponhubv0.Models.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for FileDocument metadata.
 * The actual binary data is in GridFS — this repo handles metadata queries.
 */
@Repository
public interface FileRepository extends MongoRepository<FileDocument, String> {

    /** Find metadata by the GridFS ObjectId string */
    Optional<FileDocument> findByGridFsId(String gridFsId);

    /** Find all files associated with a specific product */
    List<FileDocument> findAllByProductId(Long productId);

    /** Delete metadata entry by GridFS ObjectId */
    void deleteByGridFsId(String gridFsId);

    /** Check if a file exists by its GridFS id */
    boolean existsByGridFsId(String gridFsId);
}
