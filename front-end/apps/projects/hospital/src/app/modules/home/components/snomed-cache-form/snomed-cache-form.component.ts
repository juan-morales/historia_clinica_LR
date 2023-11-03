import { Component, EventEmitter, OnInit, Input, Output } from '@angular/core';

import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { SnomedECL, TerminologyCSVDto } from '@api-rest/api-model';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { delay, of } from 'rxjs';

@Component({
	selector: 'app-snomed-cache-form',
	templateUrl: './snomed-cache-form.component.html',
	styleUrls: ['./snomed-cache-form.component.scss']
})
export class SnomedCacheFormComponent implements OnInit {
	@Input() terminologyName: SnomedECL;
	@Output() add = new EventEmitter<TerminologyCSVDto>();

	form: UntypedFormGroup;
	loading = false;
	urlPlaceholder = '';

	constructor(
		private formBuilder: UntypedFormBuilder,
		private readonly translateService: TranslateService,
		private readonly snackBarService: SnackBarService
	) { }

	ngOnInit(): void {
		this.form = this.formBuilder.group({
			url: ['', Validators.required],
		});
		this.urlPlaceholder = `https://lamansys.nyc3.digitaloceanspaces.com/snomed/${this.terminologyName}.csv`
	}

	submit() {
		if (!this.form.valid) {
			return;
		}
		let terminology: TerminologyCSVDto = {url: this.form.controls.url.value, ecl: this.terminologyName}

		this.add.emit(terminology)
		this.loading = true;
		of(false).pipe(delay(1000)).subscribe(v => {
			this.loading = v;
			this.translateService.get("configuracion.snomed-cache.CSV_ADDED").subscribe(
				translatedText => this.snackBarService.showSuccess(translatedText)
			);
		});
	}

	hasError(type: string, control: string): boolean {
		return this.form.get(control).hasError(type);
	}

}
