import { ENursingRecordStatus } from '@api-rest/api-model';
import { Component, Input } from '@angular/core';
import { DateDto, DateTimeDto } from '@api-rest/api-model';
import { ExtraInfo, Status } from '@presentation/components/indication/indication.component';
import { MatDialog } from '@angular/material/dialog';
import { RegisterNursingRecordComponent } from '../../dialogs/register-nursing-record/register-nursing-record.component';
import { dateDtoToDate } from '@api-rest/mapper/date-dto.mapper';
import { NursingRecordFacadeService } from '../../services/nursing-record-facade.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';

@Component({
	selector: 'app-nursing-record',
	templateUrl: './nursing-record.component.html',
	styleUrls: ['./nursing-record.component.scss']
})
export class NursingRecordComponent {

	PENDING = ENursingRecordStatus.PENDING;
	internmentEpisodeId: number;

	@Input() nursingSections: NursingSections[];

	constructor(
		private readonly dialog: MatDialog,
		private readonly nursingRecordFacade: NursingRecordFacadeService,
		private readonly snackBarService: SnackBarService,
	) { }

	openDialogToRegister(nursingRecord: NursingRecord) {
		const dialogRef = this.dialog.open(RegisterNursingRecordComponent, {
			width: '60vh',
			autoFocus: false,
			disableClose: true,
			data: {
				scheduledAdministrationTime: nursingRecord.content.scheduledAdministrationTime,
				indicationDate: nursingRecord.content.indicationDate,
			}
		});
		dialogRef.afterClosed().subscribe(completedNursingRecordInformation => {
			if (completedNursingRecordInformation)
				this.nursingRecordFacade.changeStateOfNursingRecord(nursingRecord.id, completedNursingRecordInformation).subscribe(
					sucess => {
						this.snackBarService.showSuccess('indicacion.nursing-care.dialogs.register.messages.SUCCESS');
						this.nursingRecordFacade.getNursingRecords();
					},
					error => this.snackBarService.showError('indicacion.nursing-care.dialogs.register.messages.ERROR'));
		});
	}

	undoActionOfNRecord(id: number) {
		const data = { status: ENursingRecordStatus.REJECTED };
		this.nursingRecordFacade.changeStateOfNursingRecord(id, data).subscribe(
			sucess => {
				this.snackBarService.showSuccess('indicacion.nursing-care.dialogs.register.messages.SUCCESS');
				this.nursingRecordFacade.getNursingRecords();
			},
			error => this.snackBarService.showError('indicacion.nursing-care.dialogs.register.messages.ERROR'));
	}
}

export interface NursingSections {
	title: string;
	records: NursingRecord[];
	time?: number;
}

export interface NursingRecord {
	id: number;
	matIcon: string;
	svgIcon?: string;
	content: NursingRecordContent;
}

export interface NursingRecordContent {
	status: Status;
	description: string;
	extra_info?: ExtraInfo[];
	indicationDate: Date;
	scheduledAdministrationTime: DateTimeDto;
	administeredBy: string;
	administeredTime: string;
}
