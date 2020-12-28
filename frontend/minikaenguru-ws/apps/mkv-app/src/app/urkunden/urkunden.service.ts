import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class UrkundenService {


	constructor(private http: HttpClient) { }


}
