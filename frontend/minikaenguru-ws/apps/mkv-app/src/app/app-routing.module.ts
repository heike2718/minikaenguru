import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { WettbewerbInfoComponent } from './wettbewerb/wettbewerb-info.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SessionTimeoutComponent } from './session-timeout/session-timeout.component';
import { environment } from '../environments/environment';



const routes: Routes = [

	{
		path: 'landing',
		pathMatch: 'full',
		component: LandingComponent
	},
	{
		path: 'info',
		pathMatch: 'full',
		component: WettbewerbInfoComponent
	},
	{
		path: 'dashboard',
		pathMatch: 'full',
		component: DashboardComponent
	},
	{
		path: 'registrierung',
		pathMatch: 'full',
		component: RegistrationComponent
	},
	{
		path: 'timeout',
		pathMatch: 'full',
		component: SessionTimeoutComponent
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
		path: 'teilnahmen',
		loadChildren: () => import('./teilnahmen/teilnahmen.module').then(m => m.TeilnahmenModule)
	},
	{
		path: 'loesungszettel',
		loadChildren: () => import('./loesungszettel/loesungszettel.module').then(m => m.LoesungszettelModule)
	},
	{
		path: 'urkunden',
		loadChildren: () => import('./urkunden/urkunden.module').then(m => m.UrkundenModule)
	},
	{
		path: 'kinder',
		loadChildren: () => import('./kinder/kinder.module').then(m => m.KinderModule)
	},
	{
		path: 'klassen',
		loadChildren: () => import('./klassen/klassen.module').then(m => m.KlassenModule)
	},
	{
		path: 'adv',
		loadChildren: () => import('./vertrag-adv/vertrag-adv.module').then(m => m.VertragAdvModule)
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
		{ enableTracing: false, useHash: true })
	],
	exports: [RouterModule]
})
export class AppRoutingModule { }
