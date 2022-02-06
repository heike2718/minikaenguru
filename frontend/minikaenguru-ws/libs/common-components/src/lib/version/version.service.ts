import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Message, ResponsePayload } from "@minikaenguru-ws/common-messages";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { MkComponentsConfig, MkComponentsConfigService } from "../configuration/mk-components-config";

@Injectable()
export class VersionService {

   constructor(private http: HttpClient, @Inject(MkComponentsConfigService) private config: MkComponentsConfig) { }

    public ladeExpectedGuiVersion(): Observable<string> {

        const url = this.config.baseUrl + '/guiversion';

        return this.http.get(url, { observe: 'body' }).pipe(
            map(body => body as ResponsePayload),
            map(rp => rp.message),
            map(message => this.getVersion(message))
        );
    }

    public storeGuiVersionAndReloadApp(storageKey: string, guiVersion: string): void {		
		localStorage.setItem(storageKey, guiVersion);
		window.location.reload();
	}

    private getVersion(message: Message): string {

        let result = 'X.X.X';

        if (message.level === 'INFO') {
            result = message.message;
        }

        return result;
    }
}
