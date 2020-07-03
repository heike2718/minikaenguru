import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKatalogpflege from './katalogpflege.reducer';
import {katalogpflegeItemWithIDToArray} from '../katalogpflege.model';

export const katalogflegeState = createFeatureSelector<fromKatalogpflege.KatalogpflegeState>(fromKatalogpflege.katalogpflegeFeatureKey);

export const laender = createSelector(katalogflegeState, s => katalogpflegeItemWithIDToArray(s.kataloge.laender));
export const orte = createSelector(katalogflegeState, s => katalogpflegeItemWithIDToArray(s.kataloge.orte));
export const schulen = createSelector(katalogflegeState, s => katalogpflegeItemWithIDToArray(s.kataloge.schulen));
