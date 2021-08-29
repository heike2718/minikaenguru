import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TeilnahmenJahrResolver } from './teilnahmen-jahr.resolver';
import { TeilnahmenListComponent } from './teilnahmen-list/teilnahmen-list.component';


const teilnahmenRoutes: Routes = [

	{
		path: 'teilnahmen/:id',
		component: TeilnahmenListComponent,
		resolve: {teilnahmen: TeilnahmenJahrResolver}
	}

];


@NgModule({
	imports: [
		RouterModule.forChild(teilnahmenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class TeilnahmenRoutingModule { }
