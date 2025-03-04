package farming.products.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RemoveProductDataDto {

	ProductDto product;
	List<SaleRecordsDto> saleRecords;
}
