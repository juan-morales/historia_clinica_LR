import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DocumentTypeDto } from '@api-rest/api-model';
import { InternmentEpisodeDocumentService } from '@api-rest/services/internment-episode-document.service';
import { ExtesionFile } from '@core/utils/extensionFile';
import { hasError, requiredFileType } from '@core/utils/form.utils';
import { TypeaheadOption } from '@presentation/components/typeahead/typeahead.component';

@Component({
  selector: 'app-attach-document-popup',
  templateUrl: './attach-document-popup.component.html',
  styleUrls: ['./attach-document-popup.component.scss']
})
export class AttachDocumentPopupComponent implements OnInit {

  hasError = hasError;
  form: UntypedFormGroup;
  documentTypes: TypeaheadOption<any>[];
  required: boolean = true;

  constructor(private fb: UntypedFormBuilder,
              private internmentEpisodeDocument: InternmentEpisodeDocumentService,
              public dialogRef: MatDialogRef<AttachDocumentPopupComponent>,
              @Inject(MAT_DIALOG_DATA) public data) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      fileName: new UntypedFormControl({value: this.data.file.name, disabled: true}),
      file: new UntypedFormControl(this.data.file, requiredFileType(ExtesionFile.PDF)),
      type: new UntypedFormControl(null, Validators.required)
    });
    this.setDocumentTypesFilter();
  }

  setDocumentTypesFilter() {
    this.internmentEpisodeDocument.getDocumentTypes().subscribe(response => {
      if (response.length > 0) {
        const options: TypeaheadOption<any>[] = this.setFilterValues(response);
        this.documentTypes = options;
      }
    })
  }

  setFilterValues(response) {
    const opt: TypeaheadOption<any>[] = [];
    response.map(value => {
      opt.push({
        value: value.id,
        compareValue: value.description,
        viewValue: value.description,
      });
    })
    return opt;
  }

  save() {
    if ( ! this.form.valid) return;

    const formDataFile: FormData = new FormData();
    formDataFile.append('file', this.data.file);
    this.internmentEpisodeDocument.saveInternmentEpisodeDocument(formDataFile, this.data.internmentEpisodeId, this.form.get('type').value)
      .subscribe(resp => {
        if (resp)
          this.dialogRef.close()
      });
  }

  setDocumentType(type: DocumentTypeDto) {
    this.form.get('type').setValue(type);
  }

}