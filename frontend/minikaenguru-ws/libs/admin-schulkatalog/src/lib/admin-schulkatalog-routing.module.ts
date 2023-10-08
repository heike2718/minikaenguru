import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { AdminKatalogeGuardService } from "./admin-kataloge-guard.service";
import { LaenderListComponent } from "./laender/laender-list/laender-list.component";
import { LaenderListResolver } from "./laender/laender-list/laender-list.resolver";
import { LandDetailsComponent } from "./laender/land-details/land-details.component";
import { OrtDetailsComponent } from "./orte/ort-details/ort-details.component";
import { SchuleDetailsComponent } from "./schulen/schule-details/schule-details.component";
import { EditSchuleComponent } from "./schulen/edit-schule/edit-schule.component";


const routes: Routes = [
	{
		path: 'laender',
		canActivate: [AdminKatalogeGuardService],
		component: LaenderListComponent,
		resolve: { laender: LaenderListResolver },
	},
	{
		path: 'land',
		canActivate: [AdminKatalogeGuardService],
		component: LandDetailsComponent
	},
	{
		path: 'ort',
		canActivate: [AdminKatalogeGuardService],
		component: OrtDetailsComponent
	},
	{
		path: 'schule',
		canActivate: [AdminKatalogeGuardService],
		component: SchuleDetailsComponent
	},
	{
		path: 'schule-editor',
		canActivate: [AdminKatalogeGuardService],
		component: EditSchuleComponent
	},
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class AdminSchulkatalogRoutingModule { }