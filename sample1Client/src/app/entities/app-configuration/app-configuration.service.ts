import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IAppConfiguration } from './iapp-configuration';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class AppConfigurationService extends GenericApiService<IAppConfiguration> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'appConfiguration');
  }
}
