import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ChangeDetectorRef, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';

import { EntryComponents, TestingModule } from 'src/testing/utils';
import {
  AppConfigurationExtendedService,
  AppConfigurationDetailsExtendedComponent,
  AppConfigurationListExtendedComponent,
  AppConfigurationNewExtendedComponent,
} from '../';
import { IAppConfiguration } from 'src/app/entities/app-configuration';
import { ListFiltersComponent, ServiceUtils } from 'src/app/common/shared';
import { ListComponent, DetailsComponent, NewComponent, FieldsComp } from 'src/app/common/general-components';

describe('AppConfigurationListExtendedComponent', () => {
  let fixture: ComponentFixture<AppConfigurationListExtendedComponent>;
  let component: AppConfigurationListExtendedComponent;
  let el: HTMLElement;

  describe('Unit tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [AppConfigurationListExtendedComponent, ListComponent],
        imports: [TestingModule],
        providers: [AppConfigurationExtendedService, ChangeDetectorRef],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AppConfigurationListExtendedComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  });

  describe('Integration tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [
          AppConfigurationListExtendedComponent,
          AppConfigurationNewExtendedComponent,
          NewComponent,
          AppConfigurationDetailsExtendedComponent,
          ListComponent,
          DetailsComponent,
          FieldsComp,
        ].concat(EntryComponents),
        imports: [
          TestingModule,
          RouterTestingModule.withRoutes([
            { path: 'appconfiguration', component: AppConfigurationListExtendedComponent },
            { path: 'appconfiguration/:id', component: AppConfigurationDetailsExtendedComponent },
          ]),
        ],
        providers: [AppConfigurationExtendedService, ChangeDetectorRef],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AppConfigurationListExtendedComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  });
});
