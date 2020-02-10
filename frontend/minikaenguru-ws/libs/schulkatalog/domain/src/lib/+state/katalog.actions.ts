import { createAction, props } from '@ngrx/store';

export const loadLaenderkatalog = createAction(
  '[Katalog] load Länderkatalog'
);


export const loadKatalogs = createAction(
  '[Katalog] Load Katalogs'
);

export const loadKatalogsSuccess = createAction(
  '[Katalog] Load Katalogs Success',
  props<{ data: any }>()
);

export const loadKatalogsFailure = createAction(
  '[Katalog] Load Katalogs Failure',
  props<{ error: any }>()
);
