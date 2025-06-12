package com.pawar.sop.log.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "batch_type")
public class BatchType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "batch_type_id")
    private Long id;

    @Column(nullable = false)
    private String batchType;
    
    @Column(nullable = false)
    private String batchTypeDesc;
    
    @ManyToOne
	@JoinColumn(name = "sopActionTypeId")
    private SopActionType sopActionType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Column(nullable = true)
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    
    
	@Override
	public String toString() {
		return "BatchType [id=" + id + ", batchType=" + batchType + ", batchTypeDesc=" + batchTypeDesc
				+ ", sopActionType=" + sopActionType + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
}
