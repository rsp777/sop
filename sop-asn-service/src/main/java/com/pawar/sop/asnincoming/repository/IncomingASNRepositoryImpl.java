package com.pawar.sop.asnincoming.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.ASNDto;

import jakarta.persistence.EntityManager;

@Repository
public class IncomingASNRepositoryImpl implements IncomingASNRepository{

	private static final Logger logger = LoggerFactory.getLogger(IncomingASNRepositoryImpl.class);
	
	private EntityManager entityManager;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	
	public IncomingASNRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
		httpClient = HttpClients.createDefault();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}


	@Override
	public List<ASNDto> getAsn(String category) throws ClientProtocolException, IOException {
		logger.info("Category : {} ",category);
		String serviceName = "asn";
		List<ASNDto> asnDtos = fetchData(serviceName,category, List.class, ASNDto.class);
		return asnDtos;
	}
	
	public <T> List<T> fetchData(String serviceName,String category, Class<List> listClass, Class<T> class1)
			throws ClientProtocolException, IOException {
	    String json = fetch(serviceName,category);
		logger.debug("json data :" + json);
	    List<T> t = returnType(json, listClass, class1);
		return t;
	}

	public <T> String fetch(String serviceName,String category) throws ClientProtocolException, IOException {
		String url = getUrl(serviceName) + category;
		String json = restGetCall(url);
		return json;
	}

	public <T> List<T> returnType(String json, Class<List> listClass, Class<T> class1) throws JsonMappingException, JsonProcessingException {
	    try {
	        objectMapper.setSerializationInclusion(Include.NON_NULL);
	        List<T> t = objectMapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(listClass, class1));
	        logger.info("Response List : {}", t);
	        return t;
	    } catch (JsonMappingException e) {
	        logger.error("Error deserializing JSON", e);
	        return Collections.emptyList();
	    } catch (JsonProcessingException e) {
	        logger.error("Error processing JSON", e);
	        return Collections.emptyList();
	    }
	}

	public String restGetCall(String url) throws ClientProtocolException, IOException {
		logger.info("URL :" + url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		logger.info("Response : " + response.getStatusLine());
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		logger.info("json : {}",json);
		return json;
	}

	public <T> String getUrl(T serviceName) {
		if (serviceName != null) {
			String url = "http://localhost:8085/" + serviceName + "/list/category/";
			return url;
		}
		return null;
	}
	
	
	
	
}
