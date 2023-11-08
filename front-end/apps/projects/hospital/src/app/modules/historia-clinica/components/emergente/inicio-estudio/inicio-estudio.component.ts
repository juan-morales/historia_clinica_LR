import { Component, OnInit } from '@angular/core';
import { EstudiosPopupComponent } from '../pop-up/estudios-popup.component';
import { MatDialog } from '@angular/material/dialog';
import { BasicPatientDto } from '@api-rest/api-model';
import { ContextService } from '@core/services/context.service';
import { ActivatedRoute } from '@angular/router';
import { LatestStudiesComponent } from '../pop-up/latest-studies/latest-studies.component';  
@Component({
  selector: 'app-adulto-mayor',
  templateUrl: './inicio-estudio.component.html',
  styleUrls: ['./inicio-estudio.component.scss']
})
export class AdultoMayorComponent implements OnInit {
  patientId: number;
  private readonly routePrefix;
  patientData: BasicPatientDto | undefined;

  constructor(
    private dialog: MatDialog,
    private readonly contextService: ContextService,
    private readonly route: ActivatedRoute,

  ) { 
    
      
    this.routePrefix = `${this.contextService.institutionId}`;
    this.route.paramMap.subscribe(params => {
      this.patientId = Number(params.get('idPaciente'));
      });
  }

  ngOnInit(): void {
   }

  mostrarPopup(): void {
    const dialogRef = this.dialog.open(EstudiosPopupComponent, {
      width: '800px',
      data: {
        patientId: this.patientId
      }
    });

    dialogRef.afterClosed().subscribe(result => {
     });
  }
  
  mostrarPopupVerEstudios(): void {
    const dialogRef = this.dialog.open(LatestStudiesComponent, {
      width: '800px',
      data: {
        patientId: this.patientId,
        institutionId: this.routePrefix,
      }
    });
    dialogRef.afterClosed().subscribe(result => {
    });
}

}