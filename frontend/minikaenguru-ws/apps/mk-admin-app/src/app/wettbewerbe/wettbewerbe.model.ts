import { WettbewerbStatus } from '@minikaenguru-ws/common-components';

export interface Teilnahmenuebersicht {
	readonly anzahlSchulanmeldungen: number;
	readonly anzahlPrivatanmeldungen: number;
	readonly anzahlLoesungszettelSchulen: number;
	readonly anzahlLoesungszettelPrivat: number;
};

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn?: string;
	readonly wettbewerbsende?: string;
	readonly datumFreischaltungLehrer?: string;
	readonly datumFreischaltungPrivat?: string;
	readonly loesungsbuchstabenIkids: string;
	readonly loesungsbuchstabenKlasse1: string;
	readonly loesungsbuchstabenKlasse2: string;
	readonly teilnahmenuebersicht?: Teilnahmenuebersicht;
};

export interface WettbewerbWithID {
	jahr: number;
	wettbewerb: Wettbewerb;
}

export interface WettbewerbEditorModel {
	jahr: number,
	status: WettbewerbStatus,
	wettbewerbsbeginn: string
	wettbewerbsende: string;
	datumFreischaltungLehrer: string;
	datumFreischaltungPrivat: string;
	loesungsbuchstabenIkids: string;
	loesungsbuchstabenKlasse1: string;
	loesungsbuchstabenKlasse2: string;
};

export const initialWettbewerbEditorModel: WettbewerbEditorModel = {
	jahr: 0,
	status: 'ERFASST',
	wettbewerbsbeginn: '',
	wettbewerbsende: '',
	datumFreischaltungLehrer: '',
	datumFreischaltungPrivat: '',
	loesungsbuchstabenIkids: '',
	loesungsbuchstabenKlasse1: '',
	loesungsbuchstabenKlasse2: ''
}

// verpackt häufig erforderliche Operationen auf einem WettbewerbWithID[] etwas handhabbarer.
export class WettbewerbeMap {

	private wettbewerbe: Map<number, Wettbewerb> = new Map();

	constructor(readonly items: WettbewerbWithID[]) {
		if (items !== undefined) {
			for (const wb of items) {
				this.wettbewerbe.set(wb.jahr, wb.wettbewerb);
			}
		}
	}

	public has(jahr: number): boolean {

		if (jahr === NaN) {
			return false;
		}
		return this.wettbewerbe.has(jahr);
	}

	public get(jahr: number): Wettbewerb {

		if (!this.has(jahr)) {
			return null;
		}

		return this.wettbewerbe.get(jahr);
	}

	public toArray(): Wettbewerb[] {

		return [...this.wettbewerbe.values()];
	}

	public merge(wettbewerb: Wettbewerb): WettbewerbWithID[] {

		const result: WettbewerbWithID[] = [];
		for (const item of this.items) {
			if (item.jahr !== wettbewerb.jahr) {
				result.push(item);
			} else {
				result.push({ jahr: wettbewerb.jahr, wettbewerb: wettbewerb });
			}
		}
		return result;
	}
}

