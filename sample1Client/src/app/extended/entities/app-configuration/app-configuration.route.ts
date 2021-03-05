import { RouterModule, Routes } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';
import { CanDeactivateGuard } from 'src/app/common/shared';
import { AuthGuard } from 'src/app/core/auth-guard';
import {
  AppConfigurationDetailsExtendedComponent,
  AppConfigurationListExtendedComponent,
  AppConfigurationNewExtendedComponent,
} from './';

const routes: Routes = [
  {
    path: '',
    component: AppConfigurationListExtendedComponent,
    canDeactivate: [CanDeactivateGuard],
    canActivate: [AuthGuard],
  },
  {
    path: ':id',
    component: AppConfigurationDetailsExtendedComponent,
    canDeactivate: [CanDeactivateGuard],
    canActivate: [AuthGuard],
  },
  { path: 'new', component: AppConfigurationNewExtendedComponent, canActivate: [AuthGuard] },
];

export const appConfigurationRoute: ModuleWithProviders = RouterModule.forChild(routes);
