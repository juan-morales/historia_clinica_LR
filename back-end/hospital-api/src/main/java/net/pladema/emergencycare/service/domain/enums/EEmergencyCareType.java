package net.pladema.emergencycare.service.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EEmergencyCareType {

    ADULTO(1, "Adulto"),
    PEDIATRIA(2, "Pediatría"),
    GINECOLOGICA(3, "Ginecológica y obstetricia");

    private final Short id;
    private final String description;

    EEmergencyCareType(Number id, String description) {
        this.id = id.shortValue();
        this.description = description;
    }

    @JsonCreator
    public static Collection<EEmergencyCareType> getAll(){
        return Stream.of(EEmergencyCareType.values()).collect(Collectors.toList());
    }

    public Short getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
