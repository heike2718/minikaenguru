import { ErrorHandler, Injector, NgZone, Injectable } from '@angular/core';
import { LogService, MessagesService } from 'hewi-ng-lib';
import { LogPublishersService } from '../logging/log-publishers.service';
import { environment } from '../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';




@Injectable({
    providedIn: 'root'
})
export class GlobalErorHandlerService implements ErrorHandler {


    private logService: LogService;

    private messagesService: MessagesService;

    constructor(private injector: Injector, private readonly ngZone: NgZone) {

        const logPublishersService = this.injector.get(LogPublishersService);
        this.logService = this.injector.get(LogService);

        this.logService.registerPublishers(logPublishersService.publishers);
        this.logService.info('logging initialized: loglevel=' + environment.loglevel);

        this.messagesService = this.injector.get(MessagesService);
        this.logService.info('MessagesService initialized.');
    }


    handleError(error: any): void {

        let message = 'Es ist ein unerwarteter Fehler aufgetreten';

        if (error.message) {
            message += ' ' + error.message;
        }

        // ErrorHandler läuft außerhalb der Angular-Zone. message ist daher erst bei der nächsten Aktualisierung der Komponente sichtbar.
        // Das Anzeigen der Message muss daher mit ngZone.run() getriggert werden.
        // siehe https://t2informatik.de/blog/softwareentwicklung/fehlerbehandlung-in-angular-anwendungen/
        this.ngZone.run(() => this.messagesService.error(message));

        if (error instanceof HttpErrorResponse) {
            this.logService.debug('HttpErrorResponse sollte nicht vorkommen, da diese Errors vom HttpErrorService behandelt werden.');
        } else {
            // try sending an Error-Log to the Server
            // TODO: hier eine idReference mitsenden analog zu checklistenapp und profil-app
            this.logService.error('mkadmin-app - ' + message);
        }
    }
}
