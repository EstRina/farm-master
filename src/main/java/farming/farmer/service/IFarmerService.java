package farming.farmer.service;

import java.util.List;
import java.util.Set;

import farming.accounting.entity.UserAccount;
import farming.farmer.dto.FarmerDto;

public interface IFarmerService {

	FarmerDto getFarmer(Long farmerId);
	Set<FarmerDto> getFarmersByProduct(Long productId);
	List<FarmerDto> getAllFarmers();
	void createFarmerProfile(UserAccount user);
	FarmerDto updateFarmer(Long farmerId, FarmerDto dto);
	void deleteFarmer(Long farmerId);
	void addProductToFarmer(Long farmerId, Long productId);
	void removeProductFromFarmer(Long farmerId, Long productId);
	Double getFarmerBalance(Long farmerId);
	
}
