import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AuthComponent } from './auth.component';
import { PasswordResetComponent } from './routes/password-reset/password-reset.component';
import { LoginComponent } from './routes/login/login.component';


const routes: Routes = [
	{
		path: 'auth',
		component: AuthComponent,
		children: [
			{
				path: '',
				redirectTo: 'login',
				pathMatch: 'full',
			},
			{ path: 'password-reset/:token', component: PasswordResetComponent },
			{ path: 'login', component: LoginComponent },
		]
	},
];

@NgModule({
	imports: [
		RouterModule.forChild(routes)
	],
	exports: [RouterModule]
})
export class AuthRoutingModule { }
