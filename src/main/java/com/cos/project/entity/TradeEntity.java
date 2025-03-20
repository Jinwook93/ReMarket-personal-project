package com.cos.project.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne
	@JoinColumn(name = "member1_id", nullable = true)
	private MemberEntity member1;
	
	@ManyToOne
	@JoinColumn(name = "member2_id", nullable = true)
	private MemberEntity member2;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "board_id", nullable = true)
	private BoardEntity boardEntity;
	
	@Column(nullable = true)
	private Boolean Accept1;
	@Column(nullable = true)
	private Boolean Accept2;
	
	@Column(nullable = true)
	private Boolean completed1;
	@Column(nullable = true)
	private Boolean completed2;		
	
	@Column(nullable = true)
	private Boolean Booking1;
	@Column(nullable = true)
	private Boolean Booking2;
	
	@Enumerated(EnumType.STRING)
	private TradeStatus tradeStatus;			//거래 상태
	
	@Column(nullable = true)
	@CreationTimestamp
	private Timestamp createTime;
	
	@Column(nullable = true)
	@UpdateTimestamp
	private Timestamp updateTime;
	
	
}
