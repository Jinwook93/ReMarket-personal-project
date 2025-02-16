package com.cos.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	
	@ManyToOne
	@JoinColumn(name = "board_id", nullable = true)
	private BoardEntity boardEntity;
	
	private Boolean Accept1 = false;
	private Boolean Accept2 = false;
	
	private Boolean completed1 = false;
	private Boolean completed2 = false;		
	
	private Boolean Booking1 = false;
	private Boolean Booking2 = false;
	
	@Enumerated(EnumType.STRING)
	private TradeStatus tradeStatus;			//거래 상태
	
}
