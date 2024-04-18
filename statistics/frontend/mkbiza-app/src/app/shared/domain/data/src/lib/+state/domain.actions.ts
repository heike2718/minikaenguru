import { Klassenstufe, KlassenstufeDetails, KlassenstufeGUIModel, WettbewerbDetails, WettbewerbDetailsGUIModel, WettbewerbOverview } from '@mkbiza-app/domain-model';
import { createActionGroup, emptyProps, props } from '@ngrx/store';

export const domainActions = createActionGroup({
    source: 'Domain',
    events: {
        'LOAD_WETTBEWERBE': emptyProps(),
        'WETTBEWERBE_LOADED': props<{wettbewerbe: WettbewerbOverview[]}>(),
        'LOAD_WETTBEWERB': props<{jahr: number}>(),
        'WETTBEWERB_LOADED': props<{wettbewerb: WettbewerbDetails}>(),
        'SELECT_WETTBEWERBDETAILS': props<{wettbewerbGUIModel: WettbewerbDetailsGUIModel}>(),
        'LOAD_KLASSENSTUFE': props<{jahr: number, klassenstufe: Klassenstufe}>(),
        'SELECT_KLASSENSTUFEDETAILS': props<{klassenstufeGUIModel: KlassenstufeGUIModel}>(),
        'KLASSENSTUFE_LOADED': props<{klassenstufeDetails: KlassenstufeDetails}>()
    }
});