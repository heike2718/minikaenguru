import { Observable } from 'rxjs';
import { isDefined } from './is-defined';
import { filter } from 'rxjs/operators';

export function filterDefined<T>(
    source$: Observable<T>
): Observable<NonNullable<T>> {
    return source$.pipe(
        filter(isDefined)
    );
};