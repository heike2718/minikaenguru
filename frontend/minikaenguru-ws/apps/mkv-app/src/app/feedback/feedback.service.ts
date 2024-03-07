import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Klassenstufenart } from "@minikaenguru-ws/common-components";
import { Observable, of } from "rxjs";
import { Aufgabenvorschau } from "./feedback.model";
import { environment } from "../../environments/environment";
import { LoadingIndicatorService } from "@minikaenguru-ws/shared/util-mk";


@Injectable({
    providedIn: 'root'
})
export class FeedbackService {

    #httpClient = inject(HttpClient);
    #loadingIndicatorService = inject(LoadingIndicatorService);

    #url = environment.apiUrl;

    loadAufgabenvorschau(klassenstufe: Klassenstufenart): Observable<Aufgabenvorschau> {

        const url = this.#url + '/mja-api/aufgaben/' + klassenstufe;
        const obs$ = this.#httpClient.get<Aufgabenvorschau>(url, { observe: 'body' });
        return this.#loadingIndicatorService.showLoaderUntilCompleted(obs$);
    }

}