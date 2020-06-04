import { Component, OnInit, ElementRef } from '@angular/core';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Moment } from 'moment';
import * as moment from 'moment';
import { APatientDto, BMPatientDto, GenderDto, IdentificationTypeDto, CompletePatientDto, BMPersonDto } from '@api-rest/api-model';
import { PatientService } from '@api-rest/services/patient.service';
import { scrollIntoError, hasError, VALIDATIONS, DEFAULT_COUNTRY_ID } from "@core/utils/form.utils";
import { PersonMasterDataService } from '@api-rest/services/person-master-data.service';
import { AddressMasterDataService } from '@api-rest/services/address-master-data.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { ContextService } from "@core/services/context.service";
import { PersonService } from '@api-rest/services/person.service';
import { MapperService } from '@presentation/services/mapper.service';

const ROUTE_PROFILE = 'pacientes/profile/';
const ROUTE_HOME_PATIENT = 'pacientes';

@Component({
	selector: 'app-edit-patient',
	templateUrl: './edit-patient.component.html',
	styleUrls: ['./edit-patient.component.scss']
})
export class EditPatientComponent implements OnInit {

	public form: FormGroup;
	public personResponse: BMPatientDto;
	public formSubmitted: boolean = false;
	public today: Moment = moment();
	public hasError = hasError;
	public genders: GenderDto[];
	public countries: any[];
	public provinces: any[];
	public departments: any[];
	public cities: any[];
	public identificationTypeList: IdentificationTypeDto[];
	private readonly routePrefix;
	public patientType;
	public completeDataPatient: CompletePatientDto;
	public patientId: any;


	constructor(private formBuilder: FormBuilder,
		private router: Router,
		private el: ElementRef,
		private patientService: PatientService,
		private route: ActivatedRoute,
		private personMasterDataService: PersonMasterDataService,
		private addressMasterDataService: AddressMasterDataService,
		private snackBarService: SnackBarService,
		private contextService: ContextService,
		private mapperService: MapperService,
		private personService: PersonService) {
		this.routePrefix = 'institucion/' + this.contextService.institutionId + '/';
	}

	ngOnInit(): void {
		this.route.queryParams
			.subscribe(params => {
				this.patientId = params.id;
				this.patientService.getPatientCompleteData<CompletePatientDto>(this.patientId)
					.subscribe(completeData => {
						this.completeDataPatient = completeData;
						this.personService.getCompletePerson<BMPersonDto>(completeData.person.id)
							.subscribe(personInformationData => {
								if (personInformationData.identificationTypeId)
									this.form.setControl('identificationTypeId', new FormControl(Number(personInformationData.identificationTypeId), Validators.required));
								this.form.setControl('identificationNumber', new FormControl(personInformationData.identificationNumber, [Validators.required, Validators.maxLength(VALIDATIONS.MAX_LENGTH.identif_number)]));
								//person
								this.form.setControl('firstName', new FormControl(completeData.person.firstName, Validators.required));
								this.form.setControl('middleNames', new FormControl(personInformationData.middleNames));
								this.form.setControl('lastName', new FormControl(completeData.person.lastName, Validators.required));
								this.form.setControl('otherLastNames', new FormControl(personInformationData.otherLastNames));
								this.form.setControl('mothersLastName', new FormControl(personInformationData.mothersLastName));
								if (completeData.person.gender.id)
									this.form.setControl('genderId', new FormControl(Number(completeData.person.gender.id), Validators.required));
								this.form.setControl('genderSelfDeterminationId', new FormControl(Number(personInformationData.genderSelfDeterminationId)));
								this.form.setControl('nameSelfDetermination', new FormControl(personInformationData.nameSelfDetermination));
								this.form.setControl('birthDate', new FormControl(new Date(personInformationData.birthDate), Validators.required));
								this.form.setControl('cuil', new FormControl(personInformationData.cuil, Validators.maxLength(VALIDATIONS.MAX_LENGTH.cuil)));
								this.form.setControl('email', new FormControl(personInformationData.email, Validators.email));
								this.form.setControl('phoneNumber', new FormControl(personInformationData.phoneNumber));
								this.form.setControl('religion', new FormControl(personInformationData.religion));
								this.form.setControl('ethnic', new FormControl(personInformationData.ethnic));
								//medical
								this.form.setControl('medicalCoverageName', new FormControl(completeData.medicalCoverageName, Validators.maxLength(VALIDATIONS.MAX_LENGTH.medicalCoverageName)));
								this.form.setControl('medicalCoverageAffiliateNumber', new FormControl(completeData.medicalCoverageAffiliateNumber, Validators.maxLength(VALIDATIONS.MAX_LENGTH.medicalCoverageAffiliateNumber)));
								//address
								this.form.setControl('addressCountryId', new FormControl(DEFAULT_COUNTRY_ID));
								if (personInformationData.province !== undefined) {
									this.form.setControl('addressProvinceId', new FormControl(personInformationData.province.id));
									this.setDepartments();
								}
								if (personInformationData.department !== undefined) {
									this.form.setControl('addressDepartmentId', new FormControl(personInformationData.department.id));
									this.setCities();
								}
								this.form.setControl('addressCityId', new FormControl(personInformationData.cityId));
								this.form.setControl('addressStreet', new FormControl(personInformationData.street));
								this.form.setControl('addressNumber', new FormControl(personInformationData.number));
								this.form.setControl('addressFloor', new FormControl(personInformationData.floor));
								this.form.setControl('addressApartment', new FormControl(personInformationData.apartment));
								this.form.setControl('addressQuarter', new FormControl(personInformationData.quarter));
								this.form.setControl('addressPostcode', new FormControl(personInformationData.postcode));
								//doctors
								this.form.setControl('generalPractitioner', new FormControl(completeData.generalPractitioner?.fullName));
								this.form.setControl('generalPractitionerPhoneNumber', new FormControl(completeData.generalPractitioner?.phoneNumber));
								this.form.setControl('pamiDoctor', new FormControl(completeData.pamiDoctor?.fullName));
								this.form.setControl('pamiDoctorPhoneNumber', new FormControl(completeData.pamiDoctor?.phoneNumber));


							});
						this.patientType = completeData.patientType.id;
					});
				this.formBuil();
			});

		this.personMasterDataService.getGenders()
			.subscribe(genders => {
				this.genders = genders;
			});

		this.personMasterDataService.getIdentificationTypes()
			.subscribe(identificationTypes => {
				this.identificationTypeList = identificationTypes;
			});

		this.addressMasterDataService.getAllCountries()
			.subscribe(countries => {
				this.countries = countries;
				this.form.controls.addressCountryId.setValue(DEFAULT_COUNTRY_ID);
				this.setProvinces();
			});

	}

	formBuil() {
		this.form = this.formBuilder.group({
			firstName: [null, [Validators.required]],
			middleNames: [null],
			lastName: [null, [Validators.required]],
			otherLastNames: [null],
			genderId: [null, [Validators.required]],
			identificationNumber: [null, [Validators.required, Validators.maxLength(VALIDATIONS.MAX_LENGTH.identif_number)]],
			identificationTypeId: [null, [Validators.required]],
			birthDate: [null, [Validators.required]],

			//Person extended
			cuil: [null, [Validators.maxLength(VALIDATIONS.MAX_LENGTH.cuil)]],
			mothersLastName: [],
			phoneNumber: [],
			email: [null, Validators.email],
			ethnic: [],
			religion: [],
			nameSelfDetermination: [],
			genderSelfDeterminationId: [],

			//Address
			addressStreet: [],
			addressNumber: [],
			addressFloor: [],
			addressApartment: [],
			addressQuarter: [],
			addressCityId: [],
			addressPostcode: [],

			addressProvinceId: [],
			addressCountryId: [],
			addressDepartmentId: [],

			//Patient
			medicalCoverageName: [null, Validators.maxLength(VALIDATIONS.MAX_LENGTH.medicalCoverageName)],
			medicalCoverageAffiliateNumber: [null, Validators.maxLength(VALIDATIONS.MAX_LENGTH.medicalCoverageAffiliateNumber)],

			//doctors
			generalPractitioner: [],
			generalPractitionerPhoneNumber: [],
			pamiDoctor: [],
			pamiDoctorPhoneNumber: []
		});
	}

	save(): void {
		this.formSubmitted = true;
		if (this.form.valid) {
			let personRequest: APatientDto = this.mapToPersonRequest();
			this.patientService.editPatient(personRequest, this.patientId)
				.subscribe(patientId => {
					this.router.navigate([this.routePrefix + ROUTE_PROFILE + patientId]);
					this.snackBarService.showSuccess('pacientes.edit.messages.SUCCESS');
				}, _ => this.snackBarService.showError('pacientes.edit.messages.ERROR'));
		} else {
			scrollIntoError(this.form, this.el);
		}
	}

	private mapToPersonRequest(): APatientDto {
		return {
			birthDate: this.form.controls.birthDate.value,
			firstName: this.form.controls.firstName.value,
			genderId: this.form.controls.genderId.value,
			identificationTypeId: this.form.controls.identificationTypeId.value,
			identificationNumber: this.form.controls.identificationNumber.value,
			lastName: this.form.controls.lastName.value,
			middleNames: this.form.controls.middleNames.value && this.form.controls.middleNames.value.length ? this.form.controls.middleNames.value : null,
			otherLastNames: this.form.controls.otherLastNames.value && this.form.controls.otherLastNames.value.length ? this.form.controls.otherLastNames.value : null,
			//Person extended
			cuil: this.form.controls.cuil.value,
			email: this.form.controls.email.value,
			ethnic: this.form.controls.ethnic.value,
			genderSelfDeterminationId: this.form.controls.genderSelfDeterminationId.value,
			mothersLastName: this.form.controls.mothersLastName.value,
			nameSelfDetermination: this.form.controls.nameSelfDetermination.value,
			phoneNumber: this.form.controls.phoneNumber.value,
			religion: this.form.controls.religion.value,
			//Address
			apartment: this.form.controls.addressApartment.value,
			cityId: this.form.controls.addressCityId.value,
			floor: this.form.controls.addressFloor.value,
			number: this.form.controls.addressNumber.value,
			postcode: this.form.controls.addressPostcode.value,
			quarter: this.form.controls.addressQuarter.value,
			street: this.form.controls.addressStreet.value,
			//Patient
			typeId: this.patientType,
			comments: null,
			identityVerificationStatusId: null,
			medicalCoverageName: this.form.controls.medicalCoverageName.value,
			medicalCoverageAffiliateNumber: this.form.controls.medicalCoverageAffiliateNumber.value,
			//Doctors
			generalPractitioner: {
				id: this.completeDataPatient.generalPractitioner?.id,
				fullName: this.form.controls.generalPractitioner.value,
				phoneNumber: this.form.controls.generalPractitionerPhoneNumber.value,
				generalPractitioner: true
			},
			pamiDoctor: {
				id: this.completeDataPatient.pamiDoctor?.id,
				fullName: this.form.controls.pamiDoctor.value,
				phoneNumber: this.form.controls.pamiDoctorPhoneNumber.value,
				generalPractitioner: false

			}
		};

	}

	setProvinces() {
		let countryId: number = this.form.controls.addressCountryId.value;
		this.addressMasterDataService.getByCountry(countryId)
			.subscribe(provinces => {
				this.provinces = provinces
			});
	}

	setDepartments() {
		let provinceId: number = this.form.controls.addressProvinceId.value;
		this.addressMasterDataService.getDepartmentsByProvince(provinceId)
			.subscribe(departments => {
				this.departments = departments
			});
	}

	setCities() {
		let departmentId: number = this.form.controls.addressDepartmentId.value;
		this.addressMasterDataService.getCitiesByDepartment(departmentId)
			.subscribe(cities => {
				this.cities = cities;
			});
	}

	goBack(): void {
		this.formSubmitted = false;
		this.router.navigate([this.routePrefix + ROUTE_HOME_PATIENT]);
	}

}
