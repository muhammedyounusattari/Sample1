import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigurationService } from 'src/app/entities/app-configuration/app-configuration.service';
@Injectable({
  providedIn: 'root',
})
export class AppConfigurationExtendedService extends AppConfigurationService {
  constructor(protected httpclient: HttpClient) {
    super(httpclient);
    this.url += '/extended';
  }
}
