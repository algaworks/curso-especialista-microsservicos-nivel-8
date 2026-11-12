import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-pretty-status-formatter',
  templateUrl: './pretty-status-formatter.component.html',
  styleUrls: ['./pretty-status-formatter.component.css']
})
export class PrettyStatusFormatterComponent implements OnInit {

  @Input() status? : string = '';

  formattedStatus : string = '';

  constructor() { }

  ngOnInit(): void {
  }

  getStatusColorClass() : string {
    if (this.status == 'ACTIVE') {
      this.formattedStatus = 'Active';
      return 'status-success';
    }
    if (this.status == 'INACTIVE') {
      this.formattedStatus = 'Inactive';
      return 'status-danger';
    }
    if (this.status == 'PENDING_SETUP') {
      this.formattedStatus = 'Pending Setup';
      return 'status-warning';
    }

    if (this.status == 'PAID') {
      this.formattedStatus = 'Paid';
      return 'status-success';
    }
    if (this.status == 'PENDING') {
      this.formattedStatus = 'Pending';
      return 'status-warning';
    }
    if (this.status == 'PARTIALLY_PAID') {
      this.formattedStatus = 'Partially Paid';
      return 'status-warning';
    }
    if (this.status == 'CANCELLED') {
      this.formattedStatus = 'Cancelled';
      return 'status-danger';
    }
    if (this.status == 'REFUNDED') {
      this.formattedStatus = 'Refunded';
      return 'status-danger';
    }

    if (this.status == 'ENROLLED') {
      this.formattedStatus = 'Enrolled';
      return 'status-success';
    }

    if (this.status == 'ISSUED') {
      this.formattedStatus = 'Issued';
      return 'status-success';
    }

    if (this.status == 'UNPUBLISHED') {
      this.formattedStatus = 'Unpublished';
      return 'status-warning';
    }

    if (this.status == 'PUBLISHED') {
      this.formattedStatus = 'Published';
      return 'status-success';
    }

    return this.status || '-';
  }

}