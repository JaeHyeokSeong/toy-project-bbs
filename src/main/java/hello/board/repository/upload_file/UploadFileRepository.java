package hello.board.repository.upload_file;

import hello.board.entity.file.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    Optional<UploadFile> findByStoreFileName(String storeFileName);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from UploadFile uf where uf.board.id = :boardId")
    void deleteAllUploadFiles(@Param("boardId") Long boardId);

    @Query("select uf from UploadFile uf where uf.storeFileName in :storeFileNames")
    List<UploadFile> findAllByStoreFileNames(@Param("storeFileNames") List<String> storeFileNames);

    @Query("select uf from UploadFile uf join uf.board b join b.member m" +
            " where uf.storeFileName in :storeFileNames and m.id = :memberId")
    List<UploadFile> findAllByStoreFileNamesAndMemberId(@Param("memberId") Long memberId,
                                                        @Param("storeFileNames") List<String> storeFileNames);
}
