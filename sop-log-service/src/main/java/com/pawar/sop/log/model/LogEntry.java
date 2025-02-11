package com.pawar.sop.log.model;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@ToString
@NoArgsConstructor
@Entity
@Table(name = "sop_log")
public class LogEntry {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "batch_id")
	private String batchId;  // New field for batch ID
	
    @Column(nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime timestamp;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "log_level")
    private String logLevel;

    @Column(nullable = false)
    private String message;

    @Column(columnDefinition = "json")
    private String metadata;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "ip_address")
    private String ipAddress;

  
    @Column(name = "module_name")
    private String moduleName;

    @Column(name = "item")
    private String item;  // New field for item

    @Column(name = "location")
    private String location;  // New field for location

    @Column(name = "asn")
    private String asn;  // New field for ASN

    @Column(name = "batch_mode")
    private String batchMode;  // New field for batch Mode

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
    
}
