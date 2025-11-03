import { Routes } from '@angular/router';
import { Sobre } from './sobre/sobre';
import { Vitrine } from './vitrine/vitrine';
import { Contato } from './contato/contato';
import { CadastroComponent } from './cadastro/cadastro';

export const routes: Routes = [
    { path: "sobre", component: Sobre },
    { path: "vitrine", component: Vitrine },
    { path: "contato", component: Contato },
    { path: "cadastro", component: CadastroComponent },
    { path: "", redirectTo: '/vitrine', pathMatch: 'full' }
];