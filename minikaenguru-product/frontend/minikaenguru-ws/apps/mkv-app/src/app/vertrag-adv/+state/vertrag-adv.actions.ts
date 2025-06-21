import { createAction, props } from '@ngrx/store';
import { VertragAdvEditorModel } from '../vertrag-adv.model'
import { Schule } from '../../lehrer/schulen/schulen.model';

export const selectSchule = createAction(
	'[VertragAdvFacade]: setSelectedSchule',
	props<{schule: Schule}>()
)

export const editorModelInitialized = createAction(
	'[VertragAdvFacade] initEditorModel',
	props<{model: VertragAdvEditorModel}>()
);

export const submitStarted = createAction(
	'[VertragAdvFacade] submitVertrag'
);

export const submitFinished = createAction(
	'[VertragAdvFacade] submitVertrag'
);

export const editVertragFinished = createAction(
	'[LogoutService] reset editVertragAdvState'
);

export const formValidated = createAction(
	'[VertragAdvFacade] markFormCompleted',
	props<{valid: boolean}>()
);

