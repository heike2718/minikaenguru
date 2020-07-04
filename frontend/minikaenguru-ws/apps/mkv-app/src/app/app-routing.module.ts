import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { environment } from '../environments/environment';
import { WettbewerbInfoComponent } from './wettbewerb/wettbewerb-info.component';
import { DashboardComponent } from './dashboard/dashboard.component';


const routes: Routes = [

	{
		path: 'landing',
		component: LandingComponent
	},
	{
		path: 'info',
		component: WettbewerbInfoComponent
	},
	{
		path: 'dashboard',
		component: DashboardComponent
	},
	{
		path: 'registrierung',
		component: RegistrationComponent
	},
	{
		path: 'lehrer',
		loadChildren: () => import('./lehrer/lehrer.module').then(m => m.LehrerModule)
	},
	{
		path: 'privat',
		loadChildren: () => import('./privatveranstalter/privatveranstalter.module').then(m => m.PrivatveranstalterModule)
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
