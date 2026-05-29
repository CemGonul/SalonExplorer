import { Routes } from '@angular/router';

import { SalonList } from './features/salons/pages/salon-list/salon-list';

export const routes: Routes = [
  {
    path: '',
    component: SalonList
  },
  {
    path: '**',
    redirectTo: ''
  }
];
