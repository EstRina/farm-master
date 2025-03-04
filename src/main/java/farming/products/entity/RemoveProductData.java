package farming.products.entity;

import farming.products.dto.RemoveProductDataDto;
import farming.products.dto.SaleRecordsDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "remove_product_data")
public class RemoveProductData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "remove_product_data_id")
    private List<SaleRecords> saleRecords;

    public RemoveProductDataDto toDto() {
        return RemoveProductDataDto.builder()
                .product(product != null ? product.toDto() : null)
                .saleRecords(saleRecords != null ? saleRecords.stream().map(SaleRecords::build).collect(Collectors.toList()) : null)
                .build();
    }

    public static RemoveProductData of(RemoveProductDataDto dto) {
        return RemoveProductData.builder()
                .saleRecords(dto.getSaleRecords() != null ? dto.getSaleRecords().stream()
                        .map(SaleRecords::of)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}