package com.pawar.sop.asnincoming.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.entity.ASNDto;
import com.pawar.sop.asnincoming.constants.AsnStatusConstants;
import com.pawar.sop.asnincoming.service.IncomingASNService;


@RestController
@RequestMapping("/asn-incoming-service")
@EnableJpaRepositories(basePackages = "com.pawar.sop.asnincoming.repository")
public class IncomingASNController {

	private final static Logger logger = LoggerFactory.getLogger(IncomingASNController.class);

	@Autowired
	private IncomingASNService incomingASNService;
	
	
	@GetMapping("/asns")
	public ResponseEntity<List<ASNDto>> getAsnByStatus(@RequestParam String category) {
		try {
			List<ASNDto> asnDto = incomingASNService.getAsn(category);
			logger.info("Incoming ASN : {}", asnDto);
			return ResponseEntity.ok(asnDto); // Return 200 OK with the LPN
		} catch ( Exception  e) {
			// LPN not found, return 404 Not Found
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
}
