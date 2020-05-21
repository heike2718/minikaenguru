import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { environment } from '../environments/environment';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';


const routes: Routes = [
  {path: '', pathMatch: 'full', component: LandingComponent},
  {path: '**', component: NotFoundComponent},
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
