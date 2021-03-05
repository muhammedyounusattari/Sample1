import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { DetailsComponent, ListComponent, FieldsComp } from 'src/app/common/general-components';

import { TestingModule, EntryComponents } from 'src/testing/utils';
import {
  AppConfigurationExtendedService,
  AppConfigurationDetailsExtendedComponent,
  AppConfigurationListExtendedComponent,
} from '../';
import { IAppConfiguration } from 'src/app/entities/app-configuration';
describe('AppConfigurationDetailsExtendedComponent', () => {
  let component: AppConfigurationDetailsExtendedComponent;
  let fixture: ComponentFixture<AppConfigurationDetailsExtendedComponent>;
  let el: HTMLElement;

  describe('Unit Tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [AppConfigurationDetailsExtendedComponent, DetailsComponent],
        imports: [TestingModule],
        providers: [AppConfigurationExtendedService],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AppConfigurationDetailsExtendedComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  });

  describe('Integration Tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [
          AppConfigurationDetailsExtendedComponent,
          AppConfigurationListExtendedComponent,
          DetailsComponent,
          ListComponent,
          FieldsComp,
        ].concat(EntryComponents),
        imports: [
          TestingModule,
          RouterTestingModule.withRoutes([
            { path: 'appconfiguration', component: AppConfigurationDetailsExtendedComponent },
            { path: 'appconfiguration/:id', component: AppConfigurationListExtendedComponent },
          ]),
        ],
        providers: [AppConfigurationExtendedService],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AppConfigurationDetailsExtendedComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  });
});
