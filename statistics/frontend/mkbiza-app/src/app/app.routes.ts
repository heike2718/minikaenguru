import { Routes } from '@angular/router';
import { HomeComponent } from './domains/home/home.component';

export const appRoutes: Routes = [
    {
      path: '',
      // canActivate: [UserLoaderGuard],
      children: [
        {
          path: '',
          component: HomeComponent,
        }        
      ],
    },
    {
      path: '**',
      component: HomeComponent
    }
  ];
