package net.pladema.template.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplateBo {

	private Long id;
	private Integer userId;
	private String name;
	private Short typeId;
	private Integer institutionId;

	@Override
	public String toString() {
		return "DocumentTemplateBo{" + "id=" + id +
				", userId=" + userId +
				", name=" + name +
				", typeId=" + typeId +
				", institutionId=" + institutionId +
				'}';
	}
}
