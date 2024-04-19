import { ChartData } from "chart.js";

export type StatusWettbewerb = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_LEHRER' | 'DOWNLOAD_PRIVAT' | 'BEENDET';
export type Klassenstufe = 'IKID' | 'EINS' | 'ZWEI';

export const BAR_BACKGROUND_COLOR_BLUE = 'rgba(54, 162, 235, 0.5)';
export const BAR_BACKGROUND_COLOR_YELLOW = 'rgba(255, 229, 169, 1)';
export const BAR_BACKGROUND_COLOR_GREENLY = 'rgba(165, 223, 222, 1)';

export const BAR_BACKGROUND_COLOR_1 = '#85CAFD';
export const BAR_BACKGROUND_COLOR_2 = '#FCC692';
export const BAR_BACKGROUND_COLOR_3 = '#FD9DB2';

// export const BAR_BACKGROUND_COLOR_BLUE = '#059BFF';
// export const BAR_BACKGROUND_COLOR_YELLOW = '#FFC234';
// export const BAR_BACKGROUND_COLOR_GREENLY = '#22CFCF';   #FD9DB2

/*
#059BFF blau
#22CFCF türkis
#FFC234 gelb
*/

export interface ChartModel {
  readonly labels: string[],
  readonly data: number[]
};

export interface Images {
  readonly imageFrage: string | null;
  readonly imageLoesung: string | null;
};

export interface Gruppierungsitem {
  readonly name: string,
  readonly anzahl: number
};

export interface WettbewerbOverview {
  readonly jahr: number,
  readonly status: StatusWettbewerb,
  readonly anzahlKinder: number,
  readonly medianeJeKlassenstufe: Gruppierungsitem[]
  readonly kinderJeKlassenstufe: Gruppierungsitem[]
};

export interface StatistikJahreChartData {
  readonly chartDataJahreAnzahlKinder: ChartData<'bar'> | undefined;
  readonly chartDataJahreKinderKlassenstufe: ChartData<'bar'> | undefined;
  readonly chartDataJahreMediane: ChartData<'bar'> | undefined;
}

export interface WettbewerbDetails {
  readonly jahr: number;
  readonly beendet: boolean;
  readonly anzahlKinderGesamt: number;
  readonly anzahlPrivatanmeldungen: number;
  readonly anzahlSchulanmeldungen: number;
  readonly teilnehmendeSchulenGesamt: number;
  readonly klassenstufen: Klassenstufe[];
  readonly schulenJeLand: Gruppierungsitem[];
  readonly kinderJeLand: Gruppierungsitem[];
  readonly kinderJeTeilnahmeart: Gruppierungsitem[];
  readonly kinderJeKlassenstufe: Gruppierungsitem[];
  readonly kinderJeSprache: Gruppierungsitem[];
  readonly medianeJeKlassenstufe: Gruppierungsitem[];
};

export interface StatistikWettbewerbChartData {
  readonly chartDataSchulenJeLand: ChartData<'bar'>;
  readonly chartDataKinderJeLand: ChartData<'bar'>;
  readonly chartModelKinderJeTeilnahmeart: ChartModel;
  readonly chartModelKinderJeKlassenstufe: ChartModel;
  readonly chartModelKinderJeSprache: ChartModel;
  readonly chartDataMediane: ChartData<'bar'>;
  readonly chartDataSchulanmeldungenVersusSchulteilnahmen: ChartData<'bar'>;
};

export interface WettbewerbDetailsGUIModel {
  readonly wettbewerb: WettbewerbDetails;
  readonly chartData: StatistikWettbewerbChartData;
};


export interface MedianUndGesamtpunkte {
  readonly medianMalTausend: number;
  readonly gesamtpunkte: number;
};

export interface Rohpunktitem {
  readonly punkte: string;
  readonly anzahl: string;
  readonly prozentrang: string;
};

export interface Aufgabendetails {
  readonly nummer: string;
  readonly punkte: number;
  readonly strafpunkte: string;
  readonly loesungsbuchstabe: string | undefined;
  readonly quelle: string | undefined;
  readonly images: Images | undefined;
  readonly anzahlenJeLoesungsbuchstabe: Gruppierungsitem[];
  readonly anzahlenJeWertungscode: Gruppierungsitem[];
};

export interface KlassenstufeDetails {
  readonly wettbewerbsjahr: string;
  readonly klassenstufe: Klassenstufe;
  readonly beendet: boolean;
  readonly startguthaben: number;
  readonly anzahlKinderGesamt: number;
  readonly medianUndGesamtpunkte: MedianUndGesamtpunkte | null;
  readonly kinderJeLand: Gruppierungsitem[];
  readonly kinderJeTeilnahmeart: Gruppierungsitem[];
  readonly kinderJeSprache: Gruppierungsitem[];
  readonly kinderJePunktintervall: Gruppierungsitem[];
  readonly rohpunkte: Rohpunktitem[];
  readonly aufgaben: Aufgabendetails[];
};

export interface StatistikKlassenstufeChartData {
  readonly chartDataKinderJeLand: ChartData<'bar'>;
  readonly chartModelKinderJeTeilnahmeart: ChartModel;
  readonly chartModelKinderJeSprache: ChartModel;
  readonly chartDataKinderJePunktintervall: ChartData<'bar'>;
  readonly chartDataMedianUndGesamtpunkte: ChartData<'bar'>;
}

export interface StatistikAufgabeChartData {
  readonly chartDataAnzahlenJeLoesungsbuchstabe: ChartData<'bar'>;
  readonly chartModelAnzahlenJeWertungscode: ChartModel;
};

export interface AufgabeGUIModel {
  readonly aufgabendetails: Aufgabendetails;
  readonly chartData: StatistikAufgabeChartData;
};

export interface KlassenstufeGUIModel {
  readonly klassenstufeDetails: KlassenstufeDetails;
  readonly chartDataKlassenstufe: StatistikKlassenstufeChartData;
  readonly aufgabenGUIModel: AufgabeGUIModel[];
};
