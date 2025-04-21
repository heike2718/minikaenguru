import { provideEffects } from "@ngrx/effects";
import { provideState } from "@ngrx/store";
import { domainFeature, DomainEffects } from '@mks/domain-data';

export const domainDataProvider = [
    provideState(domainFeature),
    provideEffects(DomainEffects)
];