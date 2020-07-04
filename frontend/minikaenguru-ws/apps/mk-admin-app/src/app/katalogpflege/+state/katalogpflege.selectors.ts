import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKatalogpflege from './katalogpflege.reducer';
import {katalogpflegeItemWithIDToArray} from '../katalogpflege.model';

export const katalogflegeState = createFeatureSelector<fromKatalogpflege.KatalogpflegeState>(fromKatalogpflege.katalogpflegeFeatureKey);

export const kataloge = createSelector(katalogflegeState, s => s.kataloge);
export const laender = createSelector(katalogflegeState, s => katalogpflegeItemWithIDToArray(s.kataloge.laender));
export const orte = createSelector(katalogflegeState, s => s.filteredOrte);
export const schulen = createSelector(katalogflegeState, s => s.filteredSchulen);

export const selectedItem = createSelector(katalogflegeState, s => s.selectedKatalogItem);
