import { Routes, RouterModule } from '@angular/router';
import { KatalogItemsSucheComponent } from './katalog-items-suche/katalog-items-suche.component';
import { NgModule } from '@angular/core';


const routes: Routes = [
    {path: 'schulkatalog', component: KatalogItemsSucheComponent}
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
    })
    export  class  KatalogRoutingModule { }
    