import { Injectable } from '@angular/core';
import { DiagnosticReportInfoDto, StudyWithoutOrderReportInfoDto, TranscribedOrderReportInfoDto } from '@api-rest/api-model';
import { CATEGORY_IMAGE, DiagnosticWithTypeReportInfoDto, E_TYPE_ORDER, InfoNewTypeOrderDto } from '../model/ImageModel';
import { STUDY_STATUS } from '@historia-clinica/modules/ambulatoria/constants/prescripciones-masterdata';

@Injectable({
  providedIn: 'root'
})
export class MapperStudyCasesService {

constructor() { }

	mapToDiagnosticWithTypeReportInfoDto(diagnosticsReport: DiagnosticReportInfoDto[], typeOrderSlected: E_TYPE_ORDER ): DiagnosticWithTypeReportInfoDto[] {
		return diagnosticsReport.map(diagnostic => {return {...diagnostic,typeOrder: typeOrderSlected, infoOrderInstances: null}})
	}

	mapDiagnosticTranscriptaToDiagnosticWithTypeReportInfoDto(diagnosticsReport: TranscribedOrderReportInfoDto[] ): DiagnosticWithTypeReportInfoDto[] {
		return diagnosticsReport.map(transcripta =>
			{
			return {
			category: CATEGORY_IMAGE ,
			creationDate: null,
			doctor: null,
			healthCondition: null,
			id: null,
			observations: null,
			serviceRequestId: null,
			snomed: {id: null, sctid: null, pt: transcripta.snomed},
			source: null,
			sourceId: null,
			statusId: transcripta.status ? STUDY_STATUS.FINAL.id : STUDY_STATUS.REGISTERED.id,
			typeOrder: E_TYPE_ORDER.TRANSCRIPTA,
			infoOrderInstances: this.mapToInfoNewTypeOrderDto(transcripta)
	}})
	}


	mapDiagnosticSinOrdenToDiagnosticWithTypeReportInfoDto(diagnosticsReport: StudyWithoutOrderReportInfoDto[]): DiagnosticWithTypeReportInfoDto[] {
		return diagnosticsReport.map(sinOrden =>
			{
			return {
			category: CATEGORY_IMAGE ,
			creationDate: null,
			doctor: null,
			healthCondition: null,
			id: null,
			observations: null,
			serviceRequestId: null,
			snomed: {id: null, sctid: null, pt: sinOrden.snomed},
			source: null,
			sourceId: null,
			statusId: sinOrden.status ? STUDY_STATUS.FINAL.id : STUDY_STATUS.REGISTERED.id,
			typeOrder: E_TYPE_ORDER.SIN_ORDEN,
			infoOrderInstances: this.mapToInfoNewTypeOrderDto(sinOrden)
	}})
	}


	mapToInfoNewTypeOrderDto(source: TranscribedOrderReportInfoDto | StudyWithoutOrderReportInfoDto ): InfoNewTypeOrderDto {
		return {
			imageId: source.imageId,
			hceDocumentDataDto: source.hceDocumentDataDto,
			status: source.status,
			seeStudy: source.seeStudy,
			viewReport: source.viewReport,
		}
	}

	mapDiagnosticSinOrdenToDiagnosticReportInfoDto(diagnosticsReport: StudyWithoutOrderReportInfoDto[]): DiagnosticReportInfoDto[] {
		return diagnosticsReport.map(sinOrden =>
			{
			return {
			category: CATEGORY_IMAGE ,
			creationDate: null,
			doctor: null,
			healthCondition: null,
			id: null,
			observations: null,
			serviceRequestId: null,
			snomed: {id: null, sctid: null, pt: sinOrden.snomed},
			source: null,
			sourceId: null,
			statusId: sinOrden.status ? STUDY_STATUS.FINAL.id : STUDY_STATUS.REGISTERED.id,
	}})
	}

	mapDiagnosticTranscriptaToDiagnosticReportInfoDto(diagnosticsReport: TranscribedOrderReportInfoDto[]): DiagnosticReportInfoDto[] {
		return diagnosticsReport.map(transcripta =>
			{
			return {
			category: CATEGORY_IMAGE ,
			creationDate: transcripta.creationDate,
			doctor: null,
			healthCondition: null,
			id: null,
			observations: null,
			serviceRequestId: null,
			snomed: {id: null, sctid: null, pt: transcripta.snomed},
			source: null,
			sourceId: null,
			statusId: transcripta.status ? STUDY_STATUS.FINAL.id : STUDY_STATUS.REGISTERED.id,
	}})
	}

}
