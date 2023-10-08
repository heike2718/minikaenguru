import { CommonModule } from "@angular/common";
import { HttpClientModule } from "@angular/common/http";
import { ModuleWithProviders, NgModule } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { AdminSchulkatalogRoutingModule } from "./admin-schulkatalog-routing.module";
import { StoreModule } from "@ngrx/store";
import * as fromSchulkatalog from './+state/admin-katalog.reducer';
import { EffectsModule } from "@ngrx/effects";
import { CommonMessagesModule } from "@minikaenguru-ws/common-messages";
import { CommonLoggingModule } from "@minikaenguru-ws/common-logging";
import { CommonAuthModule } from '@minikaenguru-ws/common-auth';
import { AdminSchulkatalogConfig, AdminSchulkatalogConfigService } from "./configuration/schulkatalog-config";
import { AdminSchulkatalogEffects } from "./+state/admin-katalog.effects";
import { LandComponent } from "./laender/land/land.component";
import { LaenderListComponent } from "./laender/laender-list/laender-list.component";
import { LandDetailsComponent } from "./laender/land-details/land-details.component";
import { OrtComponent } from "./orte/ort/ort.component";
import { OrteListComponent } from "./orte/orte-list/orte-list.component";
import { SchuleComponent } from "./schulen/schule/schule.component";
import { SchulenListComponent } from "./schulen/schulen-list/schulen-list.component";
import { OrtDetailsComponent } from "./orte/ort-details/ort-details.component";
import { SchuleDetailsComponent } from "./schulen/schule-details/schule-details.component";
import { CommonComponentsModule } from "@minikaenguru-ws/common-components";
import { EditSchuleComponent } from "./schulen/edit-schule/edit-schule.component";

@NgModule({
    imports: [
        CommonModule,
        // FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        AdminSchulkatalogRoutingModule,
        StoreModule.forFeature(fromSchulkatalog.adminSchulkatalogFeatureKey, fromSchulkatalog.reducer),
        EffectsModule.forFeature([AdminSchulkatalogEffects]),
        CommonComponentsModule,
        CommonMessagesModule,
        CommonLoggingModule,
        CommonAuthModule,        
    ],
    declarations: [
        LandComponent,
        LaenderListComponent,
        LandDetailsComponent,
        OrtComponent,
        OrteListComponent,
        OrtDetailsComponent,
        SchuleComponent, 
        SchulenListComponent,
        SchuleDetailsComponent,
        EditSchuleComponent             
    ],
    exports: [

    ]
})
export class AdminSchulkatalogModule {

    static forRoot(config: AdminSchulkatalogConfig): ModuleWithProviders<AdminSchulkatalogModule> {
        return {
            ngModule: AdminSchulkatalogModule,
            providers: [
                {
                    provide: AdminSchulkatalogConfigService,
                    useValue: config
                }
            ]
        }
    }
}