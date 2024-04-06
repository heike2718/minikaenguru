import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { WettbewerbOverview } from "../../../model/src/lib/domain-model";
import { HttpClient, HttpHeaders } from "@angular/common/http";


@Injectable({
    providedIn: 'root'
})
export class MkbizaAPIHttpService {

    #http = inject(HttpClient);
    #url = 'mkbiza-api';

    loadWettbewerbe(): Observable<WettbewerbOverview[]> {

        const url = this.#url + '/latexlogs/';

        const headers = new HttpHeaders().set('Accept', 'application/json');

        return this.#http.get<WettbewerbOverview[]>(url, { headers: headers });
    }

}