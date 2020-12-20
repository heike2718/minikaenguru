import { ThrowStmt } from '@angular/compiler';

export type Empfaengertyp = 'ALLE' | 'LEHRER' | 'PRIVATVERANSTALTER' | 'TEST';


export interface Newsletter {
	readonly uuid: string;
	readonly betreff: string;
	readonly text: string;
	readonly versandinfoIDs: string[];
};

export interface NewsletterWithID {
	readonly uuid: string;
	readonly newsletter: Newsletter;
};

export const initialNewsletterEditorModel: Newsletter = {
	uuid: 'neu',
	betreff: 'Minikänguru: ',
	text: '',
	versandinfoIDs: []
};

export interface Versandinfo {
	readonly uuid: string;
	readonly newsletterID: string;
	readonly empfaengertyp: Empfaengertyp;
	readonly anzahlAktuellVersendet: number;
	readonly anzahlEmpaenger: number;
	readonly versandBegonnenAm: string;
	readonly versandBeendetAm: string;
	readonly versandMitFehler: boolean;
};

export interface VersandinfoWithID {
	readonly uuid: string;
	readonly versandinfo: Versandinfo;
};

export interface NewsletterVersandauftrag {
	readonly newsletterID: string;
	readonly emfaengertyp: Empfaengertyp;
};




export class NewsletterMap {

	private newsletters: Map<string,Newsletter> = new Map();

	constructor(readonly items: NewsletterWithID[]) {

		if (items !== undefined) {
			for (const nl of items) {
				this.newsletters.set(nl.uuid, nl.newsletter);
			}
		}
	}

	public has(uuid: string): boolean {

		if (!uuid) {
			return false;
		}

		return this.newsletters.has(uuid);
	}

	public get(uuid: string): Newsletter {

		if (!this.has(uuid)) {
			return null;
		}

		return this.newsletters.get(uuid);
	}

	public toArray(): Newsletter[] {

		return [...this.newsletters.values()];
	}


	public merge(newsletter: Newsletter): NewsletterWithID[] {

		const result: NewsletterWithID[] = [];

		if (!this.has(newsletter.uuid)) {
			result.push({uuid: newsletter.uuid, newsletter: newsletter});
		}

		for(const item of this.items) {
			if (item.uuid !== newsletter.uuid) {
				result.push(item);
			} else {
				result.push({uuid: newsletter.uuid, newsletter: newsletter});
			}
		}

		return result;
	}

	public remove(newsletter: Newsletter): NewsletterWithID[] {

		const result: NewsletterWithID[] = [];

		for(const item of this.items) {
			if (item.uuid !== newsletter.uuid) {
				result.push(item);
			}
		}

		return result;
	}

};

export class VersandinfoMap {

	private versandinfos: Map<string,Versandinfo> = new Map();

	constructor(readonly items: VersandinfoWithID[]) {

		if (items !== undefined) {
			for (const v of items) {
				this.versandinfos.set(v.uuid, v.versandinfo);
			}
		}
	}

	public has(uuid: string): boolean {

		if (!uuid) {
			return false;
		}

		return this.versandinfos.has(uuid);
	}

	public get(uuid: string): Versandinfo {

		if (!this.has(uuid)) {
			return null;
		}

		return this.versandinfos.get(uuid);
	}

	public toArray(): Versandinfo[] {

		return [...this.versandinfos.values()];
	}


	public merge(versandinfo: Versandinfo): VersandinfoWithID[] {

		const result: VersandinfoWithID[] = [];

		for(const item of this.items) {
			if (item.uuid !== versandinfo.uuid) {
				result.push(item);
			} else {
				result.push({uuid: versandinfo.uuid, versandinfo: versandinfo});
			}
		}

		return result;
	}
};



