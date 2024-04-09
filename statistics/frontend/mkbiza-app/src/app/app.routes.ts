import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { WettbewerbDetailsComponent } from './wettbewerbe/wettbewerb-details/wettbewerb-details/wettbewerb-details.component';

export const appRoutes: Routes = [
  { path: 'startseite', component: HomeComponent },
  { path: 'wettbewerbe/:id', component: WettbewerbDetailsComponent },
  { path: '',   redirectTo: '/startseite', pathMatch: 'full' }, // redirect to 'startseite'
  { path: '**', component: HomeComponent },  // Wildcard route keine 404-Seite nötig, landen stets wieder auf der HomeComponent
];
