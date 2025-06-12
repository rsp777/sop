package com.pawar.sop.realtime.assign.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.sop.realtime.assign.model.SopEligibleItems;
import com.pawar.sop.realtime.assign.repository.SopEligibleItemsRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SopEligibleItemsServiceImpl implements SopEligibleItemsService {

	@Autowired
	private SopEligibleItemsRepository sopEligibleItemsRepository;

	@Override
	public SopEligibleItemsDto findBySopEligibleItemsId(Integer sopEligibleItemsId) {
		SopEligibleItems sopEligibleItems = sopEligibleItemsRepository.findBysopEligibleItemsId(sopEligibleItemsId);
		SopEligibleItemsDto sopEligibleItemsDto = sopEligibleItems.convertEntityToDto(sopEligibleItems);
		return sopEligibleItemsDto;
	}

	@Override
	public List<SopEligibleItemsDto> findByAll() {
		List<SopEligibleItems> sopEligibleItems = sopEligibleItemsRepository.findAll();
		List<SopEligibleItemsDto> sopEligibleItemsDtos = new ArrayList<>();
		for (SopEligibleItems sopEligibleItems2 : sopEligibleItems) {
			SopEligibleItemsDto eligibleItemsDto = sopEligibleItems2.convertEntityToDto(sopEligibleItems2);
			sopEligibleItemsDtos.add(eligibleItemsDto);
		}
		return sopEligibleItemsDtos;
	}

	@Override
	public List<SopEligibleItemsDto> findByCategory(String category) {
		List<SopEligibleItems> sopEligibleItems = sopEligibleItemsRepository.findByCategory(category);
		List<SopEligibleItemsDto> sopEligibleItemsDtos = new ArrayList<>();
		for (SopEligibleItems sopEligibleItems2 : sopEligibleItems) {
			SopEligibleItemsDto eligibleItemsDto = sopEligibleItems2.convertEntityToDto(sopEligibleItems2);
			sopEligibleItemsDtos.add(eligibleItemsDto);
		}
		return sopEligibleItemsDtos;
	}
}