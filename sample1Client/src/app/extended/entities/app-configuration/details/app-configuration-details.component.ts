import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';

import { AppConfigurationExtendedService } from '../app-configuration.service';
import { Globals, PickerDialogService, ErrorService } from 'src/app/common/shared';

import { GlobalPermissionService } from 'src/app/core/global-permission.service';
import { AppConfigurationDetailsComponent } from 'src/app/entities/app-configuration/index';

@Component({
  selector: 'app-app-configuration-details',
  templateUrl: './app-configuration-details.component.html',
  styleUrls: ['./app-configuration-details.component.scss'],
})
export class AppConfigurationDetailsExtendedComponent extends AppConfigurationDetailsComponent implements OnInit {
  title: string = 'AppConfiguration';
  parentUrl: string = 'appconfiguration';
  //roles: IRole[];
  constructor(
    public formBuilder: FormBuilder,
    public router: Router,
    public route: ActivatedRoute,
    public dialog: MatDialog,
    public global: Globals,
    public appConfigurationExtendedService: AppConfigurationExtendedService,
    public pickerDialogService: PickerDialogService,
    public errorService: ErrorService,
    public globalPermissionService: GlobalPermissionService
  ) {
    super(
      formBuilder,
      router,
      route,
      dialog,
      global,
      appConfigurationExtendedService,
      pickerDialogService,
      errorService,
      globalPermissionService
    );
  }

  ngOnInit() {
    super.ngOnInit();
  }
}
