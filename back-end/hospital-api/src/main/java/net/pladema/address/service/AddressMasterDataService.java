package net.pladema.address.service;

import net.pladema.address.controller.service.domain.DepartmentBo;

import java.util.Collection;

public interface AddressMasterDataService {

	<T> Collection<T> findCityByProvince(Short provinceId, Class<T> clazz);

	<T> Collection<T> findDepartmentByProvince(Short provinceId, Class<T> clazz);

	<T> Collection<T> findByCountry(Short countryId, Class<T> clazz);

	<T> Collection<T> findAllCountry(Class<T> clazz);

	<T> Collection<T> findCitiesByDepartment(Short departmentId, Class<T> clazz);

	boolean existProvinceInCountry(Short countryId, Short provinceId);

	boolean existDepartmentInProvince(Short provinceId, Short departmentId);

	boolean existCityInDepartment(Short departmentId, Integer cityId);

	DepartmentBo findDepartmentById(Short departmentId);

	<T> Collection<T> findDepartmentsByProvinceIdHavingClinicalSpecialty(Short provinceId, Integer careLineId, Integer clinicalSpecialtyId, Class<T> clazz);

}
