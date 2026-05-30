import { Routes } from '@angular/router';

import { SalonDetailPage } from './features/salons/pages/salon-detail/salon-detail';
import { SalonList } from './features/salons/pages/salon-list/salon-list';

export const routes: Routes = [
  {
    path: '',
    component: SalonList
  },
  {
    path: 'salons/:id',
    component: SalonDetailPage
  },
  {
    path: '**',
    redirectTo: ''
  }
];
