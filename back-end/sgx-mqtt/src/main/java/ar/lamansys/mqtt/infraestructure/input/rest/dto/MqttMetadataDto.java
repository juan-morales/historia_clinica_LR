package ar.lamansys.mqtt.infraestructure.input.rest.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode()
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MqttMetadataDto {

    @NotNull
    @Size(min = 1,max = 255)
    private String topic;

    @NotNull
    private String message;

    @NotNull
    private boolean retained;

    @NotNull
    private Integer qos;

    @NotNull
    private String type;

}