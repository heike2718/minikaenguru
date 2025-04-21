export { domainActions } from './lib/+state/domain.actions';
export { fromDomain} from './lib/+state/domain.selectors';

// exportieren, damit der dataProvider in die API kann.
export { domainFeature } from './lib/+state/domain.reducer';
export { DomainEffects } from './lib/+state/domain.effects';