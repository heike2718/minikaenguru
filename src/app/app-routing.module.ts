import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AboutComponent } from './about/about.component';
import { AuthGuard } from './auth/auth.guard';


const routes: Routes = [
	{
		path: '',
		component: DashboardComponent
	},
	{
		path: 'about',
		component: AboutComponent,
		canActivate: [AuthGuard]
	},
	{
		path: '**',
		redirectTo: '/'
	}
];

@NgModule({
	imports: [RouterModule.forRoot(routes, {useHash: true})],
	exports: [RouterModule]
})
export class AppRoutingModule { }
