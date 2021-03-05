import { RouterModule, Routes } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';
// import { CanDeactivateGuard } from 'src/app/common/shared';
// import { AuthGuard } from 'src/app/core/auth-guard';

// import { AppConfigurationDetailsComponent, AppConfigurationListComponent, AppConfigurationNewComponent } from './';

const routes: Routes = [
  // { path: '', component: AppConfigurationListComponent, canDeactivate: [CanDeactivateGuard], canActivate: [ AuthGuard ] },
  // { path: ':id', component: AppConfigurationDetailsComponent, canDeactivate: [CanDeactivateGuard], canActivate: [ AuthGuard ] },
  // { path: 'new', component: AppConfigurationNewComponent, canActivate: [ AuthGuard ] },
];

export const appConfigurationRoute: ModuleWithProviders = RouterModule.forChild(routes);
