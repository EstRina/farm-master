package farming.products.entity;

import farming.customer.entity.Customer;
import farming.farmer.dto.AddressDto;
import farming.farmer.entity.Farmer;
import farming.products.dto.SaleRecordsDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "sale_records")
public class SaleRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    private LocalDateTime saleDate;

    private int saleQuantity;
    private double cost;

    public static SaleRecords of(SaleRecordsDto dto) {
        return SaleRecords.builder()
                .saleDate(dto.getSaleDate())
                .saleQuantity(dto.getSaleQuantity())
                .cost(dto.getCost())
                .build();
    }

    public SaleRecordsDto build() {
    	AddressDto farmerAddress = farmer != null && farmer.getAddress() != null 
                ? new AddressDto(farmer.getAddress().getCountry(), farmer.getAddress().getCity(), farmer.getAddress().getStreet()) 
                : null;
        return SaleRecordsDto.builder()
                .productName(product != null ? product.getProductName() : null)
                .customerId(customer != null ? customer.getId() : null)
                .farmerId(farmer != null ? farmer.getFarmerId() : null)
                .farmerAddress(farmerAddress)
                .saleDate(saleDate)
                .saleQuantity(saleQuantity)
                .cost(cost)
                .build();
    }
}