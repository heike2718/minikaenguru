import { Privatteilnahme, AnonymisierteTeilnahme } from '../wettbewerb/wettbewerb.model';

export interface Privatveranstalter {
	readonly hatZugangZuUnterlangen: boolean;
	readonly anzahlVergangeneTeilnahmen: number;
	readonly aktuellAngemeldet: boolean;
	readonly aktuelleTeilnahme?: Privatteilnahme;
	readonly anonymisierteTeilnahmenGeladen?: boolean;
	readonly anonymisierteTeilnahmen: AnonymisierteTeilnahme[];
}
