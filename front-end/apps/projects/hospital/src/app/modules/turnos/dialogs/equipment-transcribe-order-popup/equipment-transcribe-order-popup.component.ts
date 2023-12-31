import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SnomedDto, SnomedECL } from '@api-rest/api-model';
import { HCEPersonalHistoryDto } from '@api-rest/api-model';
import { HceGeneralStateService } from '@api-rest/services/hce-general-state.service';
import { hasError } from '@core/utils/form.utils';
import { PrescripcionesService } from '@historia-clinica/modules/ambulatoria/services/prescripciones.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-equipment-transcribe-order-popup',
    templateUrl: './equipment-transcribe-order-popup.component.html',
    styleUrls: ['./equipment-transcribe-order-popup.component.scss']
})
export class EquipmentTranscribeOrderPopupComponent implements OnInit {

    transcribeOrderForm: FormGroup;
    public readonly hasError = hasError;
    readonly studyECL = SnomedECL.PROCEDURE;
    readonly problemECL = SnomedECL.DIAGNOSIS;
    selectedStudy: SnomedDto = null;
    selectedProblem: SnomedDto = null;
    healthProblems = null;
    selectedFiles: File[] = [];
    selectedFilesShow: any[] = [];
    filesExtension = false;
    allowedExtensions = ['jpg','jpeg','png','pdf'];

    constructor(
        public dialogRef: MatDialogRef<EquipmentTranscribeOrderPopupComponent>,
        @Inject(MAT_DIALOG_DATA) public data,
        private readonly formBuilder: FormBuilder,
		private readonly hceGeneralStateService: HceGeneralStateService,
		private prescriptionService: PrescripcionesService,
		private readonly translateService: TranslateService
        ) { }

    ngOnInit(): void {
        this.transcribeOrderForm = this.formBuilder.group({
            study: [null, Validators.required],
            assosiatedProblem: [null, Validators.required],
            professional: [null, Validators.required],
            institution: [null]
        });
        if (this.data.transcribedOrder){
            this.selectedStudy = this.data.transcribedOrder.study;
            this.selectedProblem = this.data.transcribedOrder.problem;
            this.selectedFiles = this.data.transcribedOrder.selectedFiles;
            this.selectedFilesShow = this.data.transcribedOrder.selectedFilesShow;
            this.setFormValues(this.data.transcribedOrder);
        }

        this.getPatientHealthProblems();
    }

    private setFormValues(order){
        this.transcribeOrderForm.controls.study.setValue(order.study.pt)
        this.transcribeOrderForm.controls.assosiatedProblem.setValue(order.problem.pt)
        this.transcribeOrderForm.controls.professional.setValue(order.professional)
        this.transcribeOrderForm.controls.institution.setValue(order.institution)
    }

    private getPatientHealthProblems() {
        this.hceGeneralStateService.getActiveProblems(this.data.patientId).subscribe((activeProblems: HCEPersonalHistoryDto[]) => {
			const activeProblemsList = activeProblems.map(problem => ({id: problem.id, description: problem.snomed.pt, sctId: problem.snomed.sctid}));

			this.hceGeneralStateService.getChronicConditions(this.data.patientId).subscribe((chronicProblems: HCEPersonalHistoryDto[]) => {
				const chronicProblemsList = chronicProblems.map(problem => ({id: problem.id, description: problem.snomed.pt,  sctId: problem.snomed.sctid}));
				const healthProblems = activeProblemsList.concat(chronicProblemsList);
                this.healthProblems = healthProblems;
			});
		});
    }

    saveOrder() {

        let orderProfessional = this.transcribeOrderForm.controls.professional?.value;
        let orderInstitution = this.transcribeOrderForm.controls.institution?.value;

        this.checkForOrderDeletion();

        this.prescriptionService.createTranscribedOrder(this.data.patientId, this.selectedStudy, this.selectedProblem, orderProfessional, orderInstitution)
            .subscribe(serviceRequestId => {
                this.prescriptionService.saveAttachedFiles(this.data.patientId, serviceRequestId, this.selectedFiles).subscribe();
                let text = 'image-network.appointments.medical-order.TRANSCRIBED_ORDER';
                this.translateService.get(text).subscribe(translatedText => {
                    let transcribedOrder = {
                        study: this.selectedStudy,
                        serviceRequestId: serviceRequestId,
                        problem: this.selectedProblem,
                        professional: orderProfessional,
                        institution: orderInstitution,
                        selectedFiles: this.selectedFiles,
                        selectedFilesShow: this.selectedFilesShow
                    }
                    this.dialogRef.close({
                        transcribedOrder,
                        order: {
                            serviceRequestId: serviceRequestId,
                            studyName: this.selectedStudy.pt,
                            displayText: `${translatedText} - ${this.selectedStudy.pt}`,
                            isTranscribed: true
                    }})
            });
        })
        
    }

    checkForOrderDeletion(){
        if (this.data.transcribedOrder){
            let serviceRequestId = this.data.transcribedOrder.serviceRequestId;
            this.prescriptionService.deleteTranscribedOrder(this.data.patientId, serviceRequestId).subscribe();
        }
    }

    private checkFileExtensions(){
        this.selectedFilesShow.map(file => {
            let fileExt = file.split(".").pop();
            if (!this.allowedExtensions.includes(fileExt)){
                this.filesExtension = true;
            }
        })
    }

    isFormValid(): boolean {
        if(this.selectedFiles.length && !this.filesExtension)
            return this.transcribeOrderForm.valid;
        return false
    }

    onFileSelected($event){
        Array.from($event.target.files).forEach((file: File) => {
			this.selectedFiles.push(file);
			this.selectedFilesShow.push(file.name);
		});
        this.checkFileExtensions();
    }

    removeSelectedFile(index): void {
        this.filesExtension = false;
		this.selectedFiles.splice(index, 1);
		this.selectedFilesShow.splice(index, 1);
        this.checkFileExtensions();
	}

    handleStudySelected(study) {
		this.selectedStudy = study;
		this.transcribeOrderForm.controls.study.setValue(this.getStudyDisplayName());
	}

    handleProblemSelected(problem) {
		this.selectedProblem = problem;
		this.transcribeOrderForm.controls.assosiatedProblem.setValue(this.getProblemDisplayName());
	}

    getStudyDisplayName(): string {
		return this.selectedStudy?.pt;
	}

    getProblemDisplayName(): string {
		return this.selectedProblem?.pt;
	}

    resetStudySelector() {
        this.selectedStudy = null;
        this.transcribeOrderForm.controls.study.setValue(null);
    }

    resetProblemSelector() {
        this.selectedProblem = null;
        this.transcribeOrderForm.controls.assosiatedProblem.setValue(null);
    }
}
