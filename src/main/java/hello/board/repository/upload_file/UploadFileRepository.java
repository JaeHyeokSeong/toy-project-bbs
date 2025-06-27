package hello.board.repository.upload_file;

import hello.board.entity.file.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    Optional<UploadFile> findByStoreFileName(String storeFileName);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from UploadFile uf where uf.board.id = :boardId")
    void deleteAllUploadFiles(@Param("boardId") Long boardId);
}
