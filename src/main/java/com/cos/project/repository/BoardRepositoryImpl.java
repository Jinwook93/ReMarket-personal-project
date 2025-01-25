//package com.cos.project.repository;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import com.cos.project.entity.BoardEntity;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//
//import jakarta.persistence.EntityManager;
//
//@Repository
//public class BoardRepositoryImpl implements BoardRepositoryCustom{
//
//
//	private final JPAQueryFactory jpaQueryFactory;
//	
//	
//	
//	@Autowired
//	public BoardRepositoryImpl(EntityManager em) {
//		this.jpaQueryFactory = new JPAQueryFactory(em);
//	}
//
//
//
//
//	@Override
//	public List<BoardEntity> searchBoard(String category1, String category2, String content) {
//		
//
//		
//		
//		
//		
//		return null;
//	}
//
//}
