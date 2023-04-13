import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImageNetworkRoutingModule } from './image-network-routing.module';
import { PresentationModule } from '@presentation/presentation.module';
import { WorklistByTechnicalComponent } from './routes/worklist-by-technical/worklist-by-technical.component';

@NgModule({
	declarations: [
		WorklistByTechnicalComponent,
	],
	imports: [
		CommonModule,
		PresentationModule,
		ImageNetworkRoutingModule
	]
})

export class ImageNetworkModule { }
