import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { TestingModule, EntryComponents } from 'src/testing/utils';
import { AppConfigurationExtendedService, AppConfigurationNewExtendedComponent } from '../';
import { IAppConfiguration } from 'src/app/entities/app-configuration';
import { NewComponent, FieldsComp } from 'src/app/common/general-components';

describe('AppConfigurationNewExtendedComponent', () => {
  let component: AppConfigurationNewExtendedComponent;
  let fixture: ComponentFixture<AppConfigurationNewExtendedComponent>;

  let el: HTMLElement;

  describe('Unit tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [AppConfigurationNewExtendedComponent, NewComponent, FieldsComp],
        imports: [TestingModule],
        providers: [AppConfigurationExtendedService, { provide: MAT_DIALOG_DATA, useValue: {} }],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AppConfigurationNewExtendedComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  });

  describe('Integration tests', () => {
    describe('', () => {
      beforeEach(async(() => {
        TestBed.configureTestingModule({
          declarations: [AppConfigurationNewExtendedComponent, NewComponent, FieldsComp].concat(EntryComponents),
          imports: [TestingModule],
          providers: [AppConfigurationExtendedService, { provide: MAT_DIALOG_DATA, useValue: {} }],
        });
      }));

      beforeEach(() => {
        fixture = TestBed.createComponent(AppConfigurationNewExtendedComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
      });
    });
  });
});
