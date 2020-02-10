import * as fromKatalog from './katalog.reducer';
import { selectKatalogState } from './katalog.selectors';

describe('Katalog Selectors', () => {
  it('should select the feature state', () => {
    const result = selectKatalogState({
      [fromKatalog.katalogFeatureKey]: {}
    });

    expect(result).toEqual({});
  });
});
