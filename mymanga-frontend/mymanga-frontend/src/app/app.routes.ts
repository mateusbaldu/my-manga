import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { ActivateAccount } from './pages/activate-account/activate-account';
import { Profile } from './pages/profile/profile';
import { MangaDetail } from './pages/manga-detail/manga-detail';
import { AdminPanel } from './pages/admin-panel/admin-panel';
import { AdminManageMangas } from './pages/admin-panel/pages/admin-manage-mangas/admin-manage-mangas';
import { AdminAllOrders } from './pages/admin-panel/pages/admin-all-orders/admin-all-orders';
import { Cart } from './pages/cart/cart';
import { Orders } from './pages/orders/orders';
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ResetPassword } from './pages/reset-password/reset-password';

export const routes: Routes = [
  {
    path: '',
    component: Home,
    title: 'Home - My Mangá'
  },
  {
    path: 'login',
    component: Login,
    title: 'Login - My Mangá'
  },
  {
  path: 'forgot-password',
  component: ForgotPassword,
  title: 'Esqueci a Senha - My Mangá'
},
{
  path: 'reset-password',
  component: ResetPassword,
  title: 'Redefinir Senha - My Mangá'
},
  {
    path: 'register',
    component: Register,
    title: 'Cadastro - My Mangá'
  },
  {
    path: 'users/activate',
    component: ActivateAccount,
    title: 'Ativação de Conta - My Mangá'
  },
  {
    path: 'profile',
    component: Profile,
    canActivate: [authGuard],
    title: 'Perfil - My Mangá'
  },
  {
    path: 'cart',
    component: Cart,
    canActivate: [authGuard],
    title: 'Carrinho - My Mangá'
  },
  {
    path: 'orders',
    component: Orders,
    canActivate: [authGuard],
    title: 'Pedidos - My Mangá'
  },
  {
    path: 'manga/:id',
    component: MangaDetail,
    title: 'Detalhes do Mangá - My Mangá'
  },
  {
    path: 'admin',
    component: AdminPanel,
    canActivate: [adminGuard],
    title: 'Admin Panel - My Mangá',
    children: [
      {
        path: 'mangas',
        component: AdminManageMangas,
        title: 'Gerenciar Mangás - My Mangá'
      },
      {
        path: 'pedidos',
        component: AdminAllOrders,
        title: 'Gerenciar Pedidos - My Mangá'
      },
      {
        path: '',
        redirectTo: 'mangas',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];
