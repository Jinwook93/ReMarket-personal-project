package com.cos.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.TradeEntity;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, Long> {

	Optional<TradeEntity> findByMember1IdAndMember2IdAndBoardEntityId(Long Member1Id, Long Member2Id, Long BoardId);
}
