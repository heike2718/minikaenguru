import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuardService } from './infrastructure/auth-guard.service';
import { environment } from '../environments/environment';
import { WettbewerbInfoComponent } from './teilnahmen/wettbewerb/wettbewerb-info.component';
import { DashboardResolver } from './dashboard/dashboard.resolver';


const routes: Routes = [

	{
		path: 'info',
		component: WettbewerbInfoComponent
	},
	{
		path: 'registrierung',
		component: RegistrationComponent
	},
	{
		path: 'dashboard',
		component: DashboardComponent,
		resolve: {dashboard: DashboardResolver},
		canActivate: [AuthGuardService]
	},
	{
		path: 'schulen',
		loadChildren: () => import('./schulen/schulen.module').then(m => m.SchulenModule)
	},
	{
		path: '',
		pathMatch: 'full',
		component: LandingComponent
	},
	{
		path: '**',
		component: NotFoundComponent
	},
];

@NgModule({
	imports: [RouterModule.forRoot(
		routes,
		//   { enableTracing: !environment.production, useHash: true })
		{ enableTracing: false, useHash: true })
	],
	exports: [RouterModule]
})
export class AppRoutingModule { }
