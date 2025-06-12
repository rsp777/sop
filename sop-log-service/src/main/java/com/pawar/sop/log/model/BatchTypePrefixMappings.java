package com.pawar.sop.log.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "batch_type_prefix_mappings")
public class BatchTypePrefixMappings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "batch_type_prefix_mappings_id")
    private Long id;
    
	@ManyToOne
	@JoinColumn(name = "batch_type_id")
    private BatchType batchType;

    private String prefix;
}