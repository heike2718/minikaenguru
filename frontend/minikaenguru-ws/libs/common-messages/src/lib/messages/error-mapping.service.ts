import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Message } from '../domain/entities';


@Injectable({
	providedIn: 'root'
})
export class ErrorMappingService {


	public extractMessageObject(error: HttpErrorResponse): Message {

		if (error.error && error.error.message) {
			if (error.error.message['level']) {
				return { level: error.error.message['level'], message: error.error.message['message'] };
			} else {
				return { level: 'ERROR', message: error.error.message['message'] };
			}			
		}

		if (error.error && error.message) {			
			return { level: 'ERROR', message: error['message'] };
		}

		return { level: 'ERROR', message: 'Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de' };
	}

	public extractHttpStatusCode(error: HttpErrorResponse): number {
		return error.status;
	}
}
