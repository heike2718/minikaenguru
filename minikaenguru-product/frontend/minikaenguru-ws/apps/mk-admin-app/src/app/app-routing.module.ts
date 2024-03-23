import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuardService } from './infrastructure/auth-guard.service';
import { NotAuthorizedComponent } from './not-authorized/not-authorized.component';
import { IrgendwasListComponent } from './layouttests/irgendwas-list/irgendwas-list.component';
import { SessionTimeoutComponent } from './session-timeout/session-timeout.component';


const routes: Routes = [
	{ path: 'landing', pathMatch: 'full', component: LandingComponent },
	{ path: 'forbidden', pathMatch: 'full', component: NotAuthorizedComponent },
	{ path: 'dashboard', pathMatch: 'full', component: DashboardComponent, canActivate: [AuthGuardService] },
	{ path: 'timeout', pathMatch: 'full', component: SessionTimeoutComponent},
	{ path: 'aktuelle-meldung', loadChildren: () => import('./aktuelle-meldung/aktuelle-meldung.module').then(m => m.AktuelleMeldungModule) },
	{ path: 'wettbewerbe', loadChildren: () => import('./wettbewerbe/wettbewerbe.module').then(m => m.WettbewerbeModule) },
	{ path: 'schulkatalog', loadChildren: () => import('@minikaenguru-ws/admin-schulkatalog').then(m => m.AdminSchulkatalogModule)},
	{ path: 'veranstalter', loadChildren: () => import('./veranstalter/veranstalter.module').then(m => m.VeranstalterModule) },
	{ path: 'schulteilnahme', loadChildren: () => import('./schulteilnahmen/schulteilnahmen.module').then(m => m.SchulteilnahmenModule) },
	{ path: 'eventlog', loadChildren: () => import('./eventlog/eventlog.module').then(m => m.EventlogModule) },
	{ path: 'mustertexte', loadChildren: () => import('./mustertexte/mustertexte.module').then(m => m.MustertexteModule) },
	{ path: 'newsletters', loadChildren: () => import('./newsletter/newsletter.module').then(m => m.NewsletterModule) },
	{ path: 'versandauftraege', loadChildren: () => import('./versandauftraege/versandauftraege.module').then(m => m.VersandauftraegeModule) },
	{ path: 'uploads', loadChildren: () => import('./uploads/uploads.module').then(m => m.UploadsModule) },
	{ path: 'loesungszettel', loadChildren: () => import('./loesungszettel/loesungszettel.module').then(m => m.LoesungszettelModule) },
	{ path: 'statistik', loadChildren: () => import('./statistik/statistik.module').then(m => m.StatistikModule)},
	{ path: 'irgendwas', pathMatch: 'full', component: IrgendwasListComponent },
	{ path: '', pathMatch: 'full', component: LandingComponent },
	{ path: '**', component: NotFoundComponent },
];

@NgModule({
	imports: [RouterModule.forRoot(
		routes,
		{ enableTracing: false, useHash: true })
	],
	exports: [RouterModule]
})
export class AppRoutingModule { }
