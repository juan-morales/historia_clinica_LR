package net.pladema.emergencycare.service.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.pladema.sgx.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EEmergencyCareState {

    ATENCION(1, "En atención"),
    ESPERA(2, "En espera"),
    AUSENCIA(3, "Finalizado por ausencia"),
    ALTA(4, "Finalizado por alta");

    private final Short id;
    private final String description;

    EEmergencyCareState(Number id, String description) {
        this.id = id.shortValue();
        this.description = description;
    }

    @JsonCreator
    public static List<EEmergencyCareState> getAll(){
        return Stream.of(EEmergencyCareState.values()).collect(Collectors.toList());
    }

    @JsonCreator
    public static EEmergencyCareState getById(Short id){
        for(EEmergencyCareState ecs: values()) {
            if(ecs.id.equals(id)) return ecs;
        }
        throw new NotFoundException("emergencyCareState-not-exists", String.format("El valor %s es inválido", id));
    }

    public Short getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
