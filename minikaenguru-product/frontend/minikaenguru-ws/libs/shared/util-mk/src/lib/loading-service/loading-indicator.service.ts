import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, of } from "rxjs";
import { concatMap, finalize, tap } from 'rxjs/operators';


@Injectable({
    providedIn: 'root'
}) // singleton
export class LoadingIndicatorService {

    #loadingSubject = new BehaviorSubject<boolean>(false);

    loading$: Observable<boolean> = this.#loadingSubject.asObservable();

    showLoaderUntilCompleted<T>(obs$: Observable<T>): Observable<T> {

        // of(.) immediately emmits ist value
        // concatMap triggers the obs$ to emmit its values

        return of(null).pipe(
            tap(() => this.loadingOn()),
            concatMap(() => obs$),
            finalize(() => this.loadingOff())
        );
    }

    private loadingOn(): void {
        this.#loadingSubject.next(true);
    }


    private loadingOff(): void {
        this.#loadingSubject.next(false);
    }

}
