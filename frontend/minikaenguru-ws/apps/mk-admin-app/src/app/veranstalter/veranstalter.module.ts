import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VeranstalterListComponent } from './veranstalter-list/veranstalter-list.component';
import { FormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import { VeranstalterRoutingModule } from './veranstalter-routing.module';
import * as fromVeranstalter from './+state/veranstalter.reducer';
import { VeranstalterCardComponent } from './veranstalter-card/veranstalter-card.component';
import { VeranstalterSucheComponent } from './veranstalter-suche/veranstalter-suche.component';
import { VeranstalterComponent } from './veranstalter/veranstalter.component';
import { VeranstalterDetailsComponent } from './veranstalter-details/veranstalter-details.component';
import { VeranstalterNavComponent } from './veranstalter-nav/veranstalter-nav.component';
import { PrivatteilnahmeOverviewComponent } from './privatteilnahme-overview/privatteilnahme-overview.component';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { ZugangUnterlagenComponent } from './zugang-unterlagen/zugang-unterlagen.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';


@NgModule({
	declarations: [
		VeranstalterListComponent,
		VeranstalterCardComponent,
		VeranstalterSucheComponent,
		VeranstalterComponent,
		VeranstalterDetailsComponent,
		VeranstalterNavComponent,
		PrivatteilnahmeOverviewComponent,
		ZugangUnterlagenComponent
	],
	imports: [
		CommonModule,
		FormsModule,
		NgbModule,
		CommonComponentsModule,
		VeranstalterRoutingModule,
		StoreModule.forFeature(fromVeranstalter.veranstalterFeatureKey, fromVeranstalter.reducer),
	]
})
export class VeranstalterModule { }
