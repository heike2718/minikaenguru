import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatistikDashboardComponent } from './statistik-dashboard/statistik-dashboard.component';
import { GruppeninfoDetailsComponent } from './gruppeninfo-details/gruppeninfo-details.component';
import { StoreModule } from '@ngrx/store';
import * as fromStatistik from './+state/statistik.reducer';
import { StatistikRoutingModule } from './statistik-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StatistikItemComponent } from './statistik-item/statistik-item.component';


@NgModule({
  declarations: [
    StatistikDashboardComponent,
    GruppeninfoDetailsComponent,
    StatistikItemComponent
  ],
  imports: [
    CommonModule,
    StatistikRoutingModule,
    NgbModule,
    StoreModule.forFeature(fromStatistik.statistikFeatureKey, fromStatistik.reducer)
  ]
})
export class StatistikModule { }
