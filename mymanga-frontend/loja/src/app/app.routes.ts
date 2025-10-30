import { Routes } from '@angular/router';
import { VitrineComponent } from './pages/vitrine/vitrine.component';
import { MangaDetailComponent } from './pages/manga-detail/manga-detail.component';
import { CartComponent } from './pages/cart/cart.component';
import { AuthComponent } from './pages/auth/auth.component';
import { CheckoutComponent } from './pages/checkout/checkout.component';
import { OrdersComponent } from './pages/orders/orders.component';
import { Sobre } from './sobre/sobre';
import { Contato } from './contato/contato';

export const routes: Routes = [
    { path: '', redirectTo: '/vitrine', pathMatch: 'full' },
    { path: 'vitrine', component: VitrineComponent },
    { path: 'manga/:id', component: MangaDetailComponent },
    { path: 'cesta', component: CartComponent },
    { path: 'cadastro', component: AuthComponent },
    { path: 'checkout', component: CheckoutComponent },
    { path: 'pedidos', component: OrdersComponent },
    { path: 'sobre', component: Sobre },
    { path: 'contato', component: Contato }
];