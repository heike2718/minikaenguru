import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { WettbewerbeListComponent } from './wettbewerbe-list/wettbewerbe-list.component';


const wettbewerbeRoutes: Routes = [

	{
		path: 'wettbewerbe',
		component: WettbewerbeListComponent
	}

];


@NgModule({
	imports: [
		RouterModule.forChild(wettbewerbeRoutes)
	],
	exports: [
		RouterModule
	]
})
export class WettbewerbeRoutingModule { }
