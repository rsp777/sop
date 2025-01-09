package com.pawar.sop.asnincoming.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.entity.ASNDto;
import com.pawar.sop.asnincoming.repository.IncomingASNRepository;

@Service
public class IncomingASNService {

	private final IncomingASNRepository incomingASNRepository;

    @Autowired
    public IncomingASNService(IncomingASNRepository incomingASNRepository) {
        this.incomingASNRepository = incomingASNRepository;
    }
	
	@Transactional
	public List<ASNDto> getAsn(String category) throws ClientProtocolException, IOException {
		return incomingASNRepository.getAsn(category);
	}

}
