import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AnmeldungenListComponent } from './anmeldungen-list/anmeldungen-list.component';


const anmeldungenRoutes: Routes = [

	{
		path: 'anmeldungen',
		component: AnmeldungenListComponent
	}

];


@NgModule({
	imports: [
		RouterModule.forChild(anmeldungenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class AnmeldungenRoutingModule { }
