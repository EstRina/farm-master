package farming.products.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;
import farming.products.dto.SurpriseBagDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "surprise_bags")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SurpriseBag {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name = "Surprise Bag"; // Название по умолчанию
    private int quantity; // Ограниченное количество
    private double price = 5.0; // Низкая фиксированная цена
    private LocalDateTime startTime; // Время появления
    private LocalDateTime endTime; // Время окончания
    public String imgUrl;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return quantity > 0 && now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    public static SurpriseBag of(SurpriseBagDto dto) {
    	return SurpriseBag.builder().id(dto.getId()).name(dto.getName()).price(dto.getPrice()).quantity(dto.getQuantity())
    			.startTime(dto.getStartTime()).imgUrl(dto.getImgUrl()).endTime(dto.getEndTime()).build();
    }

    public SurpriseBagDto build() {
        return SurpriseBagDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .quantity(quantity)
                .startTime(startTime)
                .endTime(endTime)
                .imgUrl(imgUrl)
                .farmer(farmer != null ? farmer.build() : null)
                .build();
    }
}
