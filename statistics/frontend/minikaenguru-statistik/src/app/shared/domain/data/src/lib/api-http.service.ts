import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { Klassenstufe, KlassenstufeDetails, WettbewerbDetails, WettbewerbOverview } from "@mks/domain-model";
import { HttpClient, HttpHeaders } from "@angular/common/http";


@Injectable({
    providedIn: 'root'
})
export class APIHttpService {

    #http = inject(HttpClient);
    #url = '/minikaenguru-statistik/api/wettbewerbe/';

    loadWettbewerbe(): Observable<WettbewerbOverview[]> {

        const url = this.#url;
        const headers = new HttpHeaders().set('Accept', 'application/json');
        return this.#http.get<WettbewerbOverview[]>(url, { headers: headers });
    }

    loadWettbewerb(jahr: number): Observable<WettbewerbDetails>{

        const url = this.#url + jahr;
        const headers = new HttpHeaders().set('Accept', 'application/json');
        return this.#http.get<WettbewerbDetails>(url, { headers: headers });
    }

    loadKlassenstufe(jahr: number, klassenstufe: Klassenstufe): Observable<KlassenstufeDetails> {

        const url = this.#url + jahr + '/' + klassenstufe;
        const headers = new HttpHeaders().set('Accept', 'application/json');
        return this.#http.get<KlassenstufeDetails>(url, { headers: headers });
    }

}