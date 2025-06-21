import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { WettbewerbDetailsComponent } from './wettbewerbe/wettbewerb-details/wettbewerb-details.component';
import { KlassenstufeDetailsComponent } from './wettbewerbe/klassenstufen/klassenstufe-details/klassenstufe-details.component';
import { ErrorComponent } from './error/error.component';
import { klassenstufeResolver } from './wettbewerbe/klassenstufen/klassenstufe-details/klassenstufe-route.resolver';
import { wettbewerbDetailsResolver } from './wettbewerbe/wettbewerb-details/wettbewerb-detailes-route.resolver';

export const appRoutes: Routes = [
  { path: 'startseite', component: HomeComponent },
  { path: 'wettbewerbe/:id', component: WettbewerbDetailsComponent, resolve: {data: wettbewerbDetailsResolver} },
  { path: 'klassenstufen/:id/:klassenstufe', component: KlassenstufeDetailsComponent, resolve: {data: klassenstufeResolver}},
  { path: 'error', component: ErrorComponent},
  { path: '',   redirectTo: '/startseite', pathMatch: 'full' }, // redirect to 'startseite'
  { path: '**', component: HomeComponent },  // Wildcard route keine 404-Seite nötig, landen stets wieder auf der HomeComponent
];
