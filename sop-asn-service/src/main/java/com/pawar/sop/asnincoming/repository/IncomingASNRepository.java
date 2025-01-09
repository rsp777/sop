package com.pawar.sop.asnincoming.repository;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.entity.ASNDto;

public interface IncomingASNRepository{

	List<ASNDto> getAsn(String category) throws ClientProtocolException, IOException;

}
