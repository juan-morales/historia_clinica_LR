package net.pladema.sgh.app.seeds.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class AddressInfoSeed {

    private String street;

    private String number;

    private Integer cityId;

    private String postcode;
}
