import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { PrivatveranstalterGuardService } from '../infrastructure/privatveranstalter-guard.service';
import { PrivatteilnahmenListComponent } from './privatteilnahmen-list/privatteilnahmen-list.component';

const privatteilnahmenRoutes: Routes = [
	{
		path: 'privat',
		children: [
			{
				path: 'teilnahmen',
				canActivate: [PrivatveranstalterGuardService],
				component: PrivatteilnahmenListComponent,

			}
		]
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(privatteilnahmenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class PrivatteilnahmenRoutingModule {

}
