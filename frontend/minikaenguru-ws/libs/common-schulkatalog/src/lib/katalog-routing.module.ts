import { Routes, RouterModule } from '@angular/router';
import { KatalogItemsSucheComponent } from './katalog-items-suche/katalog-items-suche.component';
import { NgModule } from '@angular/core';
import { SchulkatalogAntragComponent } from './schulkatalog-antrag/schulkatalog-antrag.component';


const routes: Routes = [
	{path: 'schulkatalog', component: KatalogItemsSucheComponent},
	{path: 'antragsformular', component: SchulkatalogAntragComponent}
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
    })
    export  class  KatalogRoutingModule { }
