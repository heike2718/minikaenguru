import { RouterModule, Routes } from "@angular/router";
import { LehrerGuardService } from "../infrastructure/lehrer-guard.service";
import { FeedbackWettbewerbComponent } from "./feedback-wettbewerb/feedback-wettbewerb-component";
import { NgModule } from "@angular/core";
import { BewertungsbogenComponent } from "./bewertungsbogen/bewertungsbogen.component";


const feedbackRoutes: Routes = [
    {
        path: 'feedback',
        children: [
            {
                path: 'wettbewerb',
                canActivate: [LehrerGuardService],
                component: FeedbackWettbewerbComponent
            },
            {
                path: 'klasse',
                canActivate: [LehrerGuardService],
                component: BewertungsbogenComponent
            }
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(feedbackRoutes)
    ],
    exports: [
        RouterModule
    ]
})
export class FeedbackRoutingModule {

}
