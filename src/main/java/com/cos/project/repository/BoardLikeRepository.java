package com.cos.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cos.project.entity.BoardLikeEntity;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLikeEntity, Long> {

    @Query("SELECT b FROM BoardLikeEntity b WHERE b.boardEntity.id = :board_id AND b.memberEntity.id = :member_id")
    Optional<BoardLikeEntity> findBoardLikeEntity(@Param("board_id") Long board_id, @Param("member_id") Long member_id);

    @Modifying
    @Transactional
    @Query("DELETE FROM BoardLikeEntity b WHERE b.boardEntity.id = :board_id AND b.memberEntity.id = :member_id AND b.flag = :flag")
    void deleteBoardLikeEntity(@Param("board_id") Long board_id, @Param("member_id") Long member_id, @Param("flag") boolean flag);

    @Query("SELECT b FROM BoardLikeEntity b WHERE b.boardEntity.id = :board_id AND b.flag = :flag")
    List<BoardLikeEntity> findByBoardId(@Param("board_id") Long board_id, @Param("flag") boolean flag);
}
