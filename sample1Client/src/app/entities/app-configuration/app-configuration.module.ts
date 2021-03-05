import { NgModule } from '@angular/core';

import { AppConfigurationDetailsComponent, AppConfigurationListComponent, AppConfigurationNewComponent } from './';
import { appConfigurationRoute } from './app-configuration.route';

import { SharedModule } from 'src/app/common/shared';
import { GeneralComponentsModule } from 'src/app/common/general-components/general.module';

const entities = [AppConfigurationDetailsComponent, AppConfigurationListComponent, AppConfigurationNewComponent];
@NgModule({
  declarations: entities,
  exports: entities,
  imports: [appConfigurationRoute, SharedModule, GeneralComponentsModule],
})
export class AppConfigurationModule {}
