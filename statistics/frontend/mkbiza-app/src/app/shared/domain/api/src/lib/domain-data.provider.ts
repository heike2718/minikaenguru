import { provideEffects } from "@ngrx/effects";
import { provideState } from "@ngrx/store";
import { domainFeature, DomainEffects } from '@mkbiza-app/domain-data';

export const domainDataProvider = [
    provideState(domainFeature),
    provideEffects(DomainEffects)
];