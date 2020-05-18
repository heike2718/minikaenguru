import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { SchulenListComponent } from './schulen-list/schulen-list.component';
import { SchuleDashboardComponent } from './schule-dashboard/schule-dashboard.component';


const schulenRoutes: Routes = [
	{
		path: '',
		component: SchulenListComponent,
		data: { animation: 'schulen' }
	}, {
		path: 'schule-dashboard',
		component: SchuleDashboardComponent,
		data: { animation: 'schule-dashboard' }
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(schulenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class SchulenRoutingModule {

}
