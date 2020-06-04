package net.pladema.address.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5023340278494944407L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "street", length = 35, nullable = false)
    private String street;

    @Column(name = "number", length = 5, nullable = false)
    private String number;

    @Column(name = "floor", length = 2)
    private String floor;

    @Column(name = "apartment", length = 3)
    private String apartment;

    @Column(name = "quarter", length = 30)
    private String quarter;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Column(name = "postcode", length = 6, nullable = false)
    private String postcode;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;
    
    public static Address buildDummy() {
		Address newAddress = new Address();
		newAddress.setStreet("");
		newAddress.setNumber("");
		newAddress.setCityId(1);
		newAddress.setPostcode("");
        newAddress.setLatitud(10.3);
        newAddress.setLongitud(14.3);
        return newAddress;
    }

}
