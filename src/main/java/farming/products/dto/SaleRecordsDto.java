package farming.products.dto;

import java.time.LocalDateTime;

import farming.farmer.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SaleRecordsDto {
	
    private String productName;  
    private Long customerId;
    private Long farmerId;
    private AddressDto farmerAddress;
    private LocalDateTime saleDate;
    private int saleQuantity;
    private double cost;
}