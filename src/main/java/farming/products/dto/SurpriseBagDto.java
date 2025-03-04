package farming.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import farming.farmer.dto.FarmerDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SurpriseBagDto {
	

    private Long id;
    private String name;
    private int quantity;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    FarmerDto farmer;
    String imgUrl;
  
    }