import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';



const routes: Routes = [
	{ path: 'landing', pathMatch: 'full', component: LandingComponent },
	{ path: 'anmeldungen', loadChildren: () => import('./anmeldungen/anmeldungen.module').then(m => m.AnmeldungenModule)},
	{ path: 'teilnahmen', loadChildren: () => import('./teilnahmen/teilnahmen.module').then(m => m.TeilnahmenModule)},
	{ path: 'wettbewerbe', pathMatch: 'full', loadChildren: () => import('./wettbewerbe/wettbewerbe.module').then(m => m.WettbewerbeModule)},
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
