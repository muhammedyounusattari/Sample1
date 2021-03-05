import { Component, OnInit, Inject, ViewChild, EventEmitter } from '@angular/core';
import { IFCDialogConfig } from './ifc-dialog-config';
import { Router } from '@angular/router';
import { Globals } from '../../globals';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectionList, MatListOption } from '@angular/material';
import { SelectionModel } from '@angular/cdk/collections';
@Component({
  selector: 'app-picker',
  templateUrl: './picker.component.html',
  styleUrls: ['./picker.component.scss'],
})
export class PickerComponent implements OnInit {
  onScroll = new EventEmitter();
  onSearch = new EventEmitter();

  loading = false;
  submitted = false;
  title: string;
  items: any[] = [];
  @ViewChild(MatSelectionList, { static: true }) selectionList: MatSelectionList;

  selectedItem: any;
  selectedItems: any[] = [];
  errorMessage = '';
  displayField: string;
  constructor(
    private router: Router,
    private global: Globals,
    public dialogRef: MatDialogRef<PickerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IFCDialogConfig
  ) {}

  ngOnInit() {
    if (this.data.IsSingleSelection) this.selectionList.selectedOptions = new SelectionModel<MatListOption>(false);
    this.title = this.data.Title;
    this.displayField = this.data.DisplayField;
  }

  onOk() {
    let selectedOptions = this.selectionList.selectedOptions.selected;
    if (selectedOptions.length > 0) {
      for (let option of selectedOptions) {
        this.selectedItems.push(option.value);
      }
      this.dialogRef.close(this.data.IsSingleSelection ? this.selectedItems[0] : this.selectedItems);
    }
  }
  onCancel(): void {
    this.dialogRef.close();
  }

  onTableScroll() {
    this.onScroll.emit();
  }

  onSearchChange(searchValue: string) {
    this.onSearch.emit(searchValue);
  }
}
