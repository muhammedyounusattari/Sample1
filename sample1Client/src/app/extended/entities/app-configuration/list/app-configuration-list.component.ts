import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { MatDialog } from '@angular/material';

import { Router, ActivatedRoute } from '@angular/router';

import { AppConfigurationExtendedService } from '../app-configuration.service';
import { AppConfigurationNewExtendedComponent } from '../new/app-configuration-new.component';
import { Globals, PickerDialogService, ErrorService } from 'src/app/common/shared';

import { GlobalPermissionService } from 'src/app/core/global-permission.service';
import { AppConfigurationListComponent } from 'src/app/entities/app-configuration/index';

@Component({
  selector: 'app-app-configuration-list',
  templateUrl: './app-configuration-list.component.html',
  styleUrls: ['./app-configuration-list.component.scss'],
})
export class AppConfigurationListExtendedComponent extends AppConfigurationListComponent implements OnInit {
  title: string = 'AppConfiguration';
  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public global: Globals,
    public dialog: MatDialog,
    public changeDetectorRefs: ChangeDetectorRef,
    public pickerDialogService: PickerDialogService,
    public appConfigurationService: AppConfigurationExtendedService,
    public errorService: ErrorService,
    public globalPermissionService: GlobalPermissionService
  ) {
    super(
      router,
      route,
      global,
      dialog,
      changeDetectorRefs,
      pickerDialogService,
      appConfigurationService,
      errorService,
      globalPermissionService
    );
  }

  ngOnInit() {
    super.ngOnInit();
  }

  addNew() {
    super.addNew(AppConfigurationNewExtendedComponent);
  }
}
