import * as fromSchulkatalog from './schulkatalog.reducer';
import { selectSchulkatalogState } from './schulkatalog.selectors';

describe('Schulkatalog Selectors', () => {
  it('should select the feature state', () => {
    const result = selectSchulkatalogState({
      [fromSchulkatalog.schulkatalogFeatureKey]: {}
    });

    expect(result).toEqual({});
  });
});
