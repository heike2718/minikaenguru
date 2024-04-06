import { WettbewerbOverview } from '@mkbiza-app/domain-model';
import { createActionGroup, emptyProps, props } from '@ngrx/store';

export const domainActions = createActionGroup({
    source: 'Domain',
    events: {
        'LOAD_WETTBEWERBE': emptyProps(),
        'WETTBEWERBE_LOADED': props<{wettbewerbe: WettbewerbOverview[]}>()
    }
});