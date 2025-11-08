import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { ActivateAccount } from './pages/activate-account/activate-account';
import { Profile } from './pages/profile/profile';
import { MangaDetail } from './pages/manga-detail/manga-detail';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';
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
    component: AdminDashboard,
    canActivate: [adminGuard],
    title: 'Admin Dashboard - My Mangá'
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];
