package com.cos.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.TradeEntity;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, Long> {

}
