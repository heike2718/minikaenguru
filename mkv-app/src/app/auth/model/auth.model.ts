import { Schule } from 'mk-ng-commons';

export interface UserProfile {
	fullName: string;
	angemeldet: boolean;
	anzahlTeilnahmen: number;
	schule?: Schule;
}

