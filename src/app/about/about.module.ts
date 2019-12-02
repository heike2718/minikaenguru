import { Routes, RouterModule } from '@angular/router';
import { AboutComponent } from './about.component';
import { AboutResolver } from './about.resolver';
import { NgModule } from '@angular/core';
import { AboutService } from './about.service';
import { CommonModule } from '@angular/common';
import { AboutEffects } from './about.effects';
import { EffectsModule } from '@ngrx/effects';
import * as fromAbout from './about.reducers';
import { StoreModule } from '@ngrx/store';


export const aboutRoutes: Routes = [
	{
		path: '',
		component: AboutComponent,
		resolve: {
			aboutInfos: AboutResolver
		}
	},
];

@NgModule({
	imports: [
		CommonModule,
		RouterModule.forChild(aboutRoutes),
		StoreModule.forFeature(fromAbout.aboutFeatureKey, fromAbout.aboutReducerWrapper),  // important; use the wrapper function here!
		EffectsModule.forFeature([AboutEffects])
	],
	declarations: [
		AboutComponent
	],
	exports: [
		AboutComponent
	],
	providers: [
		AboutService,
		AboutResolver
	]
})

export class AboutModule {

	constructor() { }
}

