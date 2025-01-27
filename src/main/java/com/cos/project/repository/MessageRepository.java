package com.cos.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.project.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>{

}
