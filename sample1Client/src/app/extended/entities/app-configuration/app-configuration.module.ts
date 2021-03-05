import { NgModule } from '@angular/core';

import {
  AppConfigurationExtendedService,
  AppConfigurationDetailsExtendedComponent,
  AppConfigurationListExtendedComponent,
  AppConfigurationNewExtendedComponent,
} from './';
import { AppConfigurationService } from 'src/app/entities/app-configuration';
import { AppConfigurationModule } from 'src/app/entities/app-configuration/app-configuration.module';
import { appConfigurationRoute } from './app-configuration.route';

import { SharedModule } from 'src/app/common/shared';
import { GeneralComponentsExtendedModule } from 'src/app/common/general-components/extended/general-extended.module';

const entities = [
  AppConfigurationDetailsExtendedComponent,
  AppConfigurationListExtendedComponent,
  AppConfigurationNewExtendedComponent,
];
@NgModule({
  declarations: entities,
  exports: entities,
  imports: [appConfigurationRoute, AppConfigurationModule, SharedModule, GeneralComponentsExtendedModule],
  providers: [{ provide: AppConfigurationService, useClass: AppConfigurationExtendedService }],
})
export class AppConfigurationExtendedModule {}
