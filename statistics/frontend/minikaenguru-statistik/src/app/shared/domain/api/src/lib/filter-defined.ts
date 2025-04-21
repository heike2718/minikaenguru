import { filter, Observable } from 'rxjs';
import { isDefined } from './is-defined';

export function filterDefined<T>(
  source$: Observable<T>
): Observable<NonNullable<T>> {
  return source$.pipe(filter(isDefined));
}
