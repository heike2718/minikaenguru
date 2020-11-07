import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { LehrerGuardService } from '../infrastructure/lehrer-guard.service';
import { KlassenListComponent } from './klassen-list/klassen-list.component';



const klassenRoutes: Routes = [

	{
		path: 'klassen/:schulkuerzel',
		canActivate: [LehrerGuardService],
		component: KlassenListComponent
	}

];


@NgModule({
	imports: [
		RouterModule.forChild(klassenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class KlassenRoutingModule {}
