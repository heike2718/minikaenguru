import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Message, ResponsePayload } from "@minikaenguru-ws/common-messages";
import { BehaviorSubject, Observable } from "rxjs";
import { map } from "rxjs/operators";
import { MkComponentsConfig, MkComponentsConfigService } from "../configuration/mk-components-config";

@Injectable()
export class VersionService {

    #expectedVersionSubject: BehaviorSubject<string> = new BehaviorSubject<string>('');

    public expectedVersion$:Observable<string> = this.#expectedVersionSubject.asObservable();

    constructor(private http: HttpClient, @Inject(MkComponentsConfigService) private config: MkComponentsConfig) { }

    public ladeExpectedGuiVersion(): void {

        const url = this.config.baseUrl + '/guiversion';

        this.http.get(url, { observe: 'body' }).pipe(
            map(body => body as ResponsePayload),
            map(rp => rp.message),
            map(message => this.getVersion(message))
        ).subscribe(
            version => this.#expectedVersionSubject.next(version)
        );
    }

    public storeGuiVersion(storageKey: string, guiVersion: string): void {
        localStorage.setItem(storageKey, guiVersion);
        // window.location.reload();
    }

    private getVersion(message: Message): string {

        let result = 'X.X.X';

        if (message.level === 'INFO') {
            result = message.message;
        }

        return result;
    }
}
