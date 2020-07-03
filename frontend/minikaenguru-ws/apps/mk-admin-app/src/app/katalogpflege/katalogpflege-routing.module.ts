import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { KatalogpflegeComponent } from './katalogpflege.component';
import { NgModule } from '@angular/core';
import { EditOrtComponent } from './edit-ort/edit-ort.component';
import { EditLandComponent } from './edit-land/edit-land.component';
import { EditSchuleComponent } from './edit-schule/edit-schule.component';
import { EditKatalogitemComponent } from './edit-katalogitem/edit-katalogitem.component';


const katalogpflegeRoutes: Routes = [
	{
		path: 'katalogpflege',
		canActivate: [AuthGuardService],
		component: KatalogpflegeComponent
	},
	{
		path: 'katalogitem',
		canActivate: [AuthGuardService],
		component: EditKatalogitemComponent
	},
	{
		path: 'land/:id',
		canActivate: [AuthGuardService],
		component: EditLandComponent
	},
	{
		path: 'ort/:id',
		canActivate: [AuthGuardService],
		component: EditOrtComponent
	},
	{
		path: 'schule/:id',
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
