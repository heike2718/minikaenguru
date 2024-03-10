import { Klassenstufenart } from "@minikaenguru-ws/common-components";

export const schwierigkeitsgradOptions: string[] = ['keine Angabe', 'viel zu leicht', 'zu leicht', 'genau richtig', 'zu schwer', 'viel zu schwer'];
export const kategorieOptions: string[] = ['keine Angabe', 'A', 'B', 'C'];
export const jaNeinOptions: string[] = ['keine Angabe', 'ja', 'nein'];
export const spassOptions: string[] = ['keine Angabe', 'sehr wenig', 'wenig', 'viel', 'sehr viel'];
export const zufriedenheitOptions: string[] = ['keine Angabe', 'sehr unzufrieden', 'unzufrieden', 'zufrieden', 'sehr zufrieden'];
export const schriftartOptions: string[] = ['keine Angabe', 'Fibel Nord', 'Fibel Süd', 'Druckschrift Leseanfänger'];

export type Aufgabenkategorie = 'NN' | 'LEICHT' | 'MITTEL' | 'SCHWER';

export type Schriftart = 'NN' | 'DRUCKSCHRIFT' | 'FIBEL_NORD' | 'FIBEL_SUED';

export interface Images {
    readonly imageFrage: string;
    readonly imageLoesung: string;
};

export interface Aufgabe {
    readonly nummer: string;
    readonly punkte: number;
    readonly loesungsbuchstabe: string;
    readonly quelle: string;
    readonly images: Images;
};

export interface Aufgabenvorschau {
    readonly klassenstufe: string;
    readonly aufgaben: Aufgabe[];
};

export interface BewertungAufgabe {
    readonly nummer: string;
    readonly scoreSchwierigkeitsgrad: number;
    readonly empfohleneKategorie?: Aufgabenkategorie;
    readonly scoreLehrplankompatibilitaet: number;
    readonly scoreVerstaendlichkeit: number;
    readonly freitext: string;
};

export interface BewertungsbogenKlassenstufe {
    readonly klassenstufe: Klassenstufenart;
    readonly scoreSpassfaktor: number;
    readonly scoreZufriedenheit: number;
    readonly bewertungenAufgaben: BewertungAufgabe[];
    readonly schriftart?: Schriftart;
    readonly freitext: string;
};

export interface BewertungAufgabeGUIModel {
    readonly nummer: string;
    readonly aufgabeKategorie: string;
    readonly scoreSchwierigkeitsgrad: number;
    readonly empfohleneKategorie?: Aufgabenkategorie;
    readonly scoreLehrplankompatibilitaet: number;
    readonly scoreVerstaendlichkeit: number;
    readonly freitext: string;
    readonly imageBase64: string;
};

export interface BewertungsbogenGUIModel {
    readonly klassenstufe: string;
    readonly bewertungsbogen: BewertungsbogenKlassenstufe;
    readonly items: BewertungAufgabeGUIModel[]
};

export function createBewertungAufgabe(aufgabe: Aufgabe): BewertungAufgabe {

    return {
        empfohleneKategorie: 'NN',
        freitext: '',
        nummer: aufgabe.nummer,
        scoreLehrplankompatibilitaet: 0,
        scoreSchwierigkeitsgrad: 0,
        scoreVerstaendlichkeit: 0
    };

}

export function createBewertungAufgabeGUIModel(aufgabe: Aufgabe): BewertungAufgabeGUIModel {

    const bewertungAufgabe: BewertungAufgabe = createBewertungAufgabe(aufgabe);
    const kategorie = aufgabe.nummer.substring(0, 1);

    return {
        nummer: aufgabe.nummer,
        aufgabeKategorie: kategorie,
        empfohleneKategorie: bewertungAufgabe.empfohleneKategorie,
        freitext: bewertungAufgabe.freitext,
        imageBase64: aufgabe.images.imageFrage,
        scoreLehrplankompatibilitaet: bewertungAufgabe.scoreLehrplankompatibilitaet,
        scoreSchwierigkeitsgrad: bewertungAufgabe.scoreSchwierigkeitsgrad,
        scoreVerstaendlichkeit: bewertungAufgabe.scoreVerstaendlichkeit
    };
}


export function mapFormValueToBewertungsbogen(klassenstufe: Klassenstufenart, formValue: any): BewertungsbogenKlassenstufe {

    const freitextWettbewerb: string = formValue.freitextWettbewerb as string ?? '';
    const items: [] = formValue.items ?? [];

    return {
        bewertungenAufgaben: items.map(i => mapFormItem(i)),
        freitext: freitextWettbewerb.trim(),
        klassenstufe: klassenstufe,
        scoreSpassfaktor: mapSpassfaktor(formValue.scoreSpass as string ?? 'keine Angabe'),
        scoreZufriedenheit: mapZufriedenheit(formValue.scoreZufriedenheit as string ?? 'keine Angabe'),
        schriftart: mapSchriftart(formValue.schriftart as string ?? 'NN')
    }
};

export function isBewertungsbogenLeer(bewertungsbogen: BewertungsbogenKlassenstufe): boolean {

    if ((bewertungsbogen.freitext ?? '').length > 0) {
        return false;
    }

    if (bewertungsbogen.schriftart) {
        return false;
    }

    const items: BewertungAufgabe[] = bewertungsbogen.bewertungenAufgaben;
    for (let i = 0; i < items.length; i++) {

        if (!isBewertungAufgabeKomplettLeer(items[i])) {
            return false;
        }
    }

    
    
    return true;
}

// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//       private functions
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


function isBewertungAufgabeKomplettLeer(bewertung: BewertungAufgabe): boolean {

    if ((bewertung.freitext ?? '').length > 0) {
        return false;
    }

    if (bewertung.empfohleneKategorie ?? '' != '') {
        return false;
    }

    if (bewertung.scoreLehrplankompatibilitaet !== 0) {
        return false;
    }

    if (bewertung.scoreSchwierigkeitsgrad !== 0) {
        return false;
    }

    if (bewertung.scoreVerstaendlichkeit !== 0) {
        return false;
    }

    return true;
}

function mapFormItem(item: any): BewertungAufgabe {

    return {
        empfohleneKategorie: mapEmpfohleneKategorie(item.kategorie ?? 'NN'),
        freitext: item.freitextAufgabe as string ?? '',
        nummer: item.nummer as string,
        scoreLehrplankompatibilitaet: mapJaNeinOption(item.lehrplan ?? 'keine Angabe'),
        scoreSchwierigkeitsgrad: mapSchwierigkeitsgrad(item.schwierigkeitsgrad ?? 'keine Angabe'),
        scoreVerstaendlichkeit: mapJaNeinOption(item.verstaendlichkeit ?? 'keine Angabe')
    };
}

function mapEmpfohleneKategorie(kategorie: string): Aufgabenkategorie | undefined {

    if ('A' === kategorie) {
        return "LEICHT";
    }

    if ('B' === kategorie) {
        return "MITTEL";
    }

    if ('C' === kategorie) {
        return "SCHWER";
    }

    return undefined;

}

function mapSpassfaktor(selectedOption: string): number {

    if ('sehr wenig' === selectedOption) {
        return 1;
    }

    if ('wenig' === selectedOption) {
        return 2;
    }

    if ('viel' === selectedOption) {
        return 3;
    }

    if ('sehr viel' === selectedOption) {
        return 4;
    }

    return 0;
};

function mapJaNeinOption(selectedOption: string): number {

    if ('ja' === selectedOption) {
        return 1;
    }

    if ('nein' === selectedOption) {
        return -1;
    }

    return 0;
}

function mapZufriedenheit(selectedOption: string): number {

    if ('sehr unzufrieden' === selectedOption) {
        return 1;
    }

    if ('unzufrieden' === selectedOption) {
        return 2;
    }

    if ('zufrieden' === selectedOption) {
        return 3;
    }

    if ('sehr zufrieden' === selectedOption) {
        return 4;
    }

    return 0;
};

function mapSchwierigkeitsgrad(selectedOption: string): number {

    if ('zu leicht' === selectedOption) {
        return 1;
    }

    if ('viel zu leicht' === selectedOption) {
        return 2;
    }

    if ('genau richtig' === selectedOption) {
        return 3;
    }

    if ('zu schwer' === selectedOption) {
        return 4;
    }

    if ('viel zu schwer' === selectedOption) {
        return 5;
    }

    return 0;
}

function mapSchriftart(selectedOption: string): Schriftart | undefined {

    // ['keine Angabe', 'Fibel Nord', 'Fibel Süd', 'Druckschrift Leseanfänger'];
    if ('Fibel Nord' === selectedOption) {
        return "FIBEL_NORD";
    }

    if ('Fibel Süd' === selectedOption) {
        return "FIBEL_SUED";
    }

    if ('Druckschrift Leseanfänger' === selectedOption) {
        return "DRUCKSCHRIFT";
    }

    return undefined;
}

