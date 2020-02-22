import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found.component';
import { LandingComponent } from './landing/landing.component';


const routes: Routes = [
  {path: 'registrierung', component: RegistrationComponent},
  {path: '', pathMatch: 'full', component: LandingComponent},
  {path: '**', component: NotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
