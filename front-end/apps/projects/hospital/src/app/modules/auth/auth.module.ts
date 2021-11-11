import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { CoreModule } from '@core/core.module';
import { PresentationModule } from '@presentation/presentation.module';

import { AuthRoutingModule } from './auth-routing.module';

import { AuthComponent } from './auth.component';
import { LoginComponent } from './routes/login/login.component';
import { PasswordResetComponent } from './routes/password-reset/password-reset.component';
import { FormInputComponent } from './components/form-input/form-input.component';
import { OauthLoginComponent } from './routes/chaco-login/oauth-login.component';
import { AccessDataResetComponent } from './routes/access-data-reset/access-data-reset.component';
import {ClipboardModule} from "@angular/cdk/clipboard";

@NgModule({
	declarations: [
		AuthComponent,
		LoginComponent,
		PasswordResetComponent,
		FormInputComponent,
		OauthLoginComponent,
  		AccessDataResetComponent
	],
	imports: [
		FormsModule,
		ReactiveFormsModule,
		CoreModule,
		PresentationModule,
		AuthRoutingModule,
		ClipboardModule,
	]
})
export class AuthModule { }
