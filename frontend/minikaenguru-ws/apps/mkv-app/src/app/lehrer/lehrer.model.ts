import { Schule } from './schulen/schulen.model';


export interface Lehrer {
	readonly hatZugangZuUnterlangen: boolean;
	readonly schulen: Schule[];
}

