import { ChartData, ChartDataset } from 'chart.js';
import { BAR_BACKGROUND_COLOR_BLUE, BAR_BACKGROUND_COLOR_GREENLY, BAR_BACKGROUND_COLOR_YELLOW, Gruppierungsitem, ChartModel, WettbewerbDetails, WettbewerbOverview, BAR_BACKGROUND_COLOR_1, BAR_BACKGROUND_COLOR_2, BAR_BACKGROUND_COLOR_3 } from './domain-model';

/*

Balkendiagramm

barChartData: ChartData<'bar'> = {
labels: ['2006', '2007', '2008', '2009', '2010', '2011', '2012'],
datasets: [
  { data: [65, 59, 80, 81, 56, 55, 40], label: 'Series A' },
  { data: [28, 48, 40, 19, 86, 27, 90], label: 'Series B' },
],
};
*/

/*

Tortendiagramm: Der Label-Text kann mittels Array umgebrochen werden

pieChartData: ChartData<'pie', number[], string | string[]> = {
    labels: [['Download', 'Sales'], ['In', 'Store', 'Sales'], 'Mail Sales'],
    datasets: [
      {
        data: [300, 500, 100],
      },
    ],
  };
*/

// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//             WettbewerbeOverview
// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/** 
 *  
*/
export function mapToChartDataJahreAnzahlKinder(wettbewerbe: WettbewerbOverview[]): ChartData<'bar'> {

    let wettbewerbeReversed: WettbewerbOverview[] = [...wettbewerbe].reverse();

    const labels: string[] = wettbewerbeReversed.map(w => w.jahr + '');
    const data: number[] = wettbewerbeReversed.map(w => w.anzahlKinder);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...data],
                label: 'Anzahl Kinder',
                backgroundColor: BAR_BACKGROUND_COLOR_1,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
};

export function mapToChartDataJahreMediane(wettbewerbe: WettbewerbOverview[]): ChartData<'bar'> {

    let wettbewerbeReversed: WettbewerbOverview[] = [...wettbewerbe].reverse();
    const labels: string[] = wettbewerbeReversed.map(w => w.jahr + '');

    const dataIKIDS = wettbewerbeReversed.map(w => w.medianeJeKlassenstufe[0] ? w.medianeJeKlassenstufe[0].anzahl / 1000 : 0);
    const dataEINS = wettbewerbeReversed.map(w => w.medianeJeKlassenstufe[1] ? w.medianeJeKlassenstufe[1].anzahl / 1000 : 0);
    const dataZWEI = wettbewerbeReversed.map(w => w.medianeJeKlassenstufe[2] ? w.medianeJeKlassenstufe[2].anzahl / 1000 : 0);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...dataIKIDS],
                label: 'Inklusion',
                backgroundColor: BAR_BACKGROUND_COLOR_1,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            },
            {
                data: [...dataEINS],
                label: 'Klasse 1',
                backgroundColor: BAR_BACKGROUND_COLOR_2,
                // borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
                borderColor: '#36A2EB',
            },
            {
                data: [...dataZWEI],
                label: 'Klasse 2',
                backgroundColor: BAR_BACKGROUND_COLOR_3,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
};

export function mapToChartDataJahreKinderKlassenstufe(wettbewerbe: WettbewerbOverview[]): ChartData<'bar'> {

    let wettbewerbeReversed: WettbewerbOverview[] = [...wettbewerbe].reverse();
    const labels: string[] = wettbewerbeReversed.map(w => w.jahr + '');

    const dataIKIDS = wettbewerbeReversed.map(w => w.kinderJeKlassenstufe[0] ? w.kinderJeKlassenstufe[0].anzahl : 0);
    const dataEINS = wettbewerbeReversed.map(w => w.kinderJeKlassenstufe[1] ? w.kinderJeKlassenstufe[1].anzahl : 0);
    const dataZWEI = wettbewerbeReversed.map(w => w.kinderJeKlassenstufe[2] ? w.kinderJeKlassenstufe[2].anzahl : 0);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...dataIKIDS],
                label: 'Inklusion',
                backgroundColor: BAR_BACKGROUND_COLOR_1,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            },
            {
                data: [...dataEINS],
                label: 'Klasse 1',
                backgroundColor: BAR_BACKGROUND_COLOR_2,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            },
            {
                data: [...dataZWEI],
                label: 'Klasse 2',
                backgroundColor: BAR_BACKGROUND_COLOR_3,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
};

// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//             WettbewerbDetails
// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

export function mapToAnmeldungenVersusTeilnahmen(wettbewerb: WettbewerbDetails): ChartData<'bar'> {

    const labels = ['angemeldet', 'teilgenommen'];
    const anzahlen: number[] = wettbewerb.schulenJeLand.map(g => g.anzahl);
    const schulenTeilgenommen = anzahlen.reduceRight((acc, cur) => acc + cur, 0);   

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [wettbewerb.anzahlSchulanmeldungen, schulenTeilgenommen],
                label: 'Schulen',
                backgroundColor: BAR_BACKGROUND_COLOR_1,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;

};

export function mapToChartDataMediane(wettbewerb: WettbewerbDetails): ChartData<'bar'> {

    const mediane: Gruppierungsitem[] = wettbewerb.medianeJeKlassenstufe;
    // const labels: string[] = ['Median', 'Gesamtpunktzahl'];
    const labels: string[] = [];    
    const dataMedian: number[] = [];
    const dataGesamtpunkte: number[] = [];

    {
        const filtered : Gruppierungsitem[] = mediane.filter(g => g.name === 'Inklusion');

        if (filtered.length > 0) { 
            labels.push(filtered[0].name);
            dataMedian.push(filtered[0].anzahl/1000);
            dataGesamtpunkte.push(36);
        };
    }

    {
        const filtered : Gruppierungsitem[] = mediane.filter(g => g.name === 'Klasse 1');

        if (filtered.length > 0) { 
            labels.push(filtered[0].name);
            dataMedian.push(filtered[0].anzahl/1000);
            dataGesamtpunkte.push(60);
        };
    }

    {
        const filtered : Gruppierungsitem[] = mediane.filter(g => g.name === 'Klasse 2');

        if (filtered.length > 0) {
            if (filtered.length > 0) {
                labels.push(filtered[0].name); 
                dataMedian.push(filtered[0].anzahl/1000);
                dataGesamtpunkte.push(75);
            };
        };
    }

    const datasets: ChartDataset<'bar', (number | [number, number] | null)[]>[] = [];


    datasets.push({
        data: [...dataMedian],
        label: 'Median',
        backgroundColor: BAR_BACKGROUND_COLOR_3
    });

    datasets.push({
        data: [...dataGesamtpunkte.map(p => p/3)],
        label: '1/3 Gesamtpunktzahl',
        backgroundColor: BAR_BACKGROUND_COLOR_1
    });

    datasets.push({
        data: [...dataGesamtpunkte],
        label: 'Gesamtpunktzahl',
        backgroundColor: BAR_BACKGROUND_COLOR_2
    });

    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: datasets
    };

    return result;
};


export function mapToChartDataLaender(gruppierungsitems: Gruppierungsitem[], chartLabel: string): ChartData<'bar'> {

    const labels: string[] = gruppierungsitems.map(g => g.name);
    const data: number[] = gruppierungsitems.map(g => g.anzahl);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...data],
                label: chartLabel,
                backgroundColor: BAR_BACKGROUND_COLOR_1,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
};


export function mapToChartDataKinderLaender(wettbewerb: WettbewerbDetails, chartLabel: string): ChartData<'bar'> {

    const privat: Gruppierungsitem[] = wettbewerb.kinderJeTeilnahmeart.filter(g => g.name === 'PRIVAT');

    let erweiterteItems: Gruppierungsitem[] = wettbewerb.schulkinderJeLand;

    if (privat.length > 0) {
        erweiterteItems = [...erweiterteItems, privat[0]];
    }
    return mapToChartDataLaender(erweiterteItems, chartLabel);
};

// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//             generische Funktionen
// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


export function mapToChartModel(items: Gruppierungsitem[]): ChartModel {

    const labels: string[] = [];
    const data: number[] = [];

    for (let i = 0; i < items.length; i++) {
        labels.push(items[i].name);
        data.push(items[i].anzahl);
    }

    return { labels, data };

};
