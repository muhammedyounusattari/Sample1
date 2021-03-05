import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { MatDialog } from '@angular/material';

import { IAppConfiguration } from '../iapp-configuration';
import { AppConfigurationService } from '../app-configuration.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AppConfigurationNewComponent } from '../new/app-configuration-new.component';
import { BaseListComponent, Globals, listColumnType, PickerDialogService, ErrorService } from 'src/app/common/shared';
import { GlobalPermissionService } from 'src/app/core/global-permission.service';

@Component({
  selector: 'app-app-configuration-list',
  templateUrl: './app-configuration-list.component.html',
  styleUrls: ['./app-configuration-list.component.scss'],
})
export class AppConfigurationListComponent extends BaseListComponent<IAppConfiguration> implements OnInit {
  title = 'AppConfiguration';
  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public global: Globals,
    public dialog: MatDialog,
    public changeDetectorRefs: ChangeDetectorRef,
    public pickerDialogService: PickerDialogService,
    public appConfigurationService: AppConfigurationService,
    public errorService: ErrorService,
    public globalPermissionService: GlobalPermissionService
  ) {
    super(
      router,
      route,
      dialog,
      global,
      changeDetectorRefs,
      pickerDialogService,
      appConfigurationService,
      errorService
    );
  }

  ngOnInit() {
    this.entityName = 'AppConfiguration';
    this.setAssociation();
    this.setColumns();
    this.primaryKeys = ['id'];
    super.ngOnInit();
  }

  setAssociation() {
    this.associations = [];
  }

  setColumns() {
    this.columns = [
      {
        column: 'id',
        searchColumn: 'id',
        label: 'id',
        sort: true,
        filter: true,
        type: listColumnType.Number,
      },
      {
        column: 'type',
        searchColumn: 'type',
        label: 'type',
        sort: true,
        filter: true,
        type: listColumnType.String,
      },
      {
        column: 'value',
        searchColumn: 'value',
        label: 'value',
        sort: true,
        filter: true,
        type: listColumnType.String,
      },
      {
        column: 'actions',
        label: 'Actions',
        sort: false,
        filter: false,
        type: listColumnType.String,
      },
    ];
    this.selectedColumns = this.columns;
    this.displayedColumns = this.columns.map((obj) => {
      return obj.column;
    });
  }
  addNew(comp) {
    if (!comp) {
      comp = AppConfigurationNewComponent;
    }
    super.addNew(comp);
  }
}
