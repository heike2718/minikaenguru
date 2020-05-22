import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { environment } from '../environments/environment';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuardService } from './infrastructure/auth-guard.service';
import { NotAuthorizedComponent } from './not-authorized/not-authorized.component';


const routes: Routes = [
	{ path: 'forbidden', pathMatch: 'full', component: NotAuthorizedComponent },
	{ path: 'dashboard', pathMatch: 'full', component: DashboardComponent, canActivate: [AuthGuardService] },
	{ path: 'wettbewerbe', loadChildren: () => import('./wettbewerbe/wettbewerbe.module').then(m => m.WettbewerbeModule) },
	{ path: '', pathMatch: 'full', component: LandingComponent },
	{ path: '**', component: NotFoundComponent },
];

@NgModule({
	imports: [RouterModule.forRoot(
		routes,
		  { enableTracing: !environment.production, useHash: true })
		// { enableTracing: false, useHash: true })
	],
	exports: [RouterModule]
})
export class AppRoutingModule { }
