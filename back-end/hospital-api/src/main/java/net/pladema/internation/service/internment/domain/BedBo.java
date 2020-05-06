package net.pladema.internation.service.internment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class BedBo implements Serializable {

    private Integer id;

    private String bedNumber;

    private RoomBo room;
}
