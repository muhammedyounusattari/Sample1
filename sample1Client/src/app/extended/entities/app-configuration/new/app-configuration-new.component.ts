import { Component, OnInit, Inject } from '@angular/core';
import { AppConfigurationExtendedService } from '../app-configuration.service';

import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { Globals, PickerDialogService, ErrorService } from 'src/app/common/shared';
import { MatDialogRef, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { GlobalPermissionService } from 'src/app/core/global-permission.service';

import { AppConfigurationNewComponent } from 'src/app/entities/app-configuration/index';

@Component({
  selector: 'app-app-configuration-new',
  templateUrl: './app-configuration-new.component.html',
  styleUrls: ['./app-configuration-new.component.scss'],
})
export class AppConfigurationNewExtendedComponent extends AppConfigurationNewComponent implements OnInit {
  title: string = 'New AppConfiguration';
  constructor(
    public formBuilder: FormBuilder,
    public router: Router,
    public route: ActivatedRoute,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<AppConfigurationNewComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public global: Globals,
    public pickerDialogService: PickerDialogService,
    public appConfigurationExtendedService: AppConfigurationExtendedService,
    public errorService: ErrorService,
    public globalPermissionService: GlobalPermissionService
  ) {
    super(
      formBuilder,
      router,
      route,
      dialog,
      dialogRef,
      data,
      global,
      pickerDialogService,
      appConfigurationExtendedService,
      errorService,
      globalPermissionService
    );
  }

  ngOnInit() {
    super.ngOnInit();
  }
}
