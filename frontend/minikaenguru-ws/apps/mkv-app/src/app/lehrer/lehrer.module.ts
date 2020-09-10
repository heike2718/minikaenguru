import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LehrerDashboardComponent } from './lehrer-dashboard/lehrer-dashboard.component';
import { LehrerDashboardResolver } from './lehrer-dashboard/lehrer-dashboard.resolver';
import { SchuleDashboardComponent } from './schulen/schule-dashboard/schule-dashboard.component';
import { SchulenListComponent } from './schulen/schulen-list/schulen-list.component';
import { SchuleCardComponent } from './schulen/schule-card/schule-card.component';
import { StoreModule } from '@ngrx/store';
import * as fromLehrer from './+state/lehrer.reducer';
import { LehrerRoutingModule } from './lehrer-routing.module';
import { SchulenListResolver } from './schulen/schulen-list/schulen-list.resolver';
import { SchuleDashboardResolver } from './schulen/schule-dashboard/schule-dashboard.resolver';
import { CommonComponentsModule  } from '@minikaenguru-ws/common-components';



@NgModule({
	imports: [
		CommonModule,
		CommonComponentsModule,
		LehrerRoutingModule,
		StoreModule.forFeature(fromLehrer.lehrerFeatureKey, fromLehrer.reducer),
	],
	declarations: [
		LehrerDashboardComponent,
		SchulenListComponent,
		SchuleDashboardComponent,
		SchuleCardComponent
	],
	exports: [
		LehrerDashboardComponent
	],
	providers: [
		LehrerDashboardResolver,
		SchuleDashboardResolver,
		SchulenListResolver
	]
})
export class LehrerModule { }
