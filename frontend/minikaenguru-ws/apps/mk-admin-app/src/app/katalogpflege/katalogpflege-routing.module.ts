import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { KatalogpflegeComponent } from './katalogpflege.component';
import { NgModule } from '@angular/core';
import { EditOrtComponent } from './orte/edit-ort/edit-ort.component';
import { EditLandComponent } from './laender/edit-land/edit-land.component';
import { EditSchuleComponent } from './schulen/edit-schule/edit-schule.component';
import { LaenderListComponent } from './laender/laender-list/laender-list.component';
import { OrteListComponent } from './orte/orte-list/orte-list.component';
import { SchulenListComponent } from './schulen/schulen-list/schulen-list.component';
import { LaenderListResolver } from './laender/laender-list/laender-list.resolver';


const katalogpflegeRoutes: Routes = [
	{
		path: 'katalogpflege',
		canActivate: [AuthGuardService],
		component: KatalogpflegeComponent
	},
	{
		path: 'laender',
		canActivate: [AuthGuardService],
		component: LaenderListComponent,
		resolve: { laender: LaenderListResolver },
	},
	{
		path: 'orte',
		canActivate: [AuthGuardService],
		component: OrteListComponent
	},
	{
		path: 'schulen',
		canActivate: [AuthGuardService],
		component: SchulenListComponent
	},
	{
		path: 'land-editor',
		canActivate: [AuthGuardService],
		component: EditLandComponent
	},
	{
		path: 'ort-editor',
		canActivate: [AuthGuardService],
		component: EditOrtComponent
	},
	{
		path: 'schule-editor',
		canActivate: [AuthGuardService],
		component: EditSchuleComponent
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(katalogpflegeRoutes)
	],
	exports: [
		RouterModule
	]
})
export class KatalogpflegeRoutingModule {}
