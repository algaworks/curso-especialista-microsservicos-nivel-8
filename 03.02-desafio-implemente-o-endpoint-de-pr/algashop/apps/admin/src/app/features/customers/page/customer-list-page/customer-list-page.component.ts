import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { CustomersService } from '../../customers.service';
import { ActivatedRoute, Router } from '@angular/router';
import { sleep } from 'src/app/shared/shared.module';
import { TableLazyLoadEvent } from 'primeng/table';
import { CustomerFilter, CustomerModel, CustomerSortProperty } from '../../models/model';

@Component({
  selector: 'app-customer-list-page',
  templateUrl: './customer-list-page.component.html',
  styleUrl: './customer-list-page.component.css'
})
export class CustomerListPageComponent implements OnInit {
  readonly sortProperties = CustomerSortProperty;
  customers: CustomerModel[] = [];
  filter: CustomerFilter = new CustomerFilter();
  totalElements = 0;
  recordsPerPage = 0;
  loading: boolean = true;

  constructor(
    private customersService: CustomersService,
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.filter.applyParams(params);
      this.search();
    });
  }

  search() {
    sleep()
      .then(() => {
        this.loadCustomers();
      });
  }

  onPageChange(event: TableLazyLoadEvent) {
    if (event.sortField) {
      let sort = event.sortField + ',' +  (event.sortOrder == 1 ? 'ASC' : 'DESC');
      this.filter.sort = sort;
    }
    this.filter.page = (event.first || 0) / (event.rows || 0);
    this.applyParamsOnRoute();
  }

  private loadCustomers() {
    firstValueFrom(this.customersService.filter(this.filter))
      .then(page => {
        this.customers = page.content;
        this.loading = false;
        this.recordsPerPage = page.size;
        this.totalElements = page.totalElements;
      })
      .catch(e => {
        let message = {
            severity:'error',
            summary: 'Server connection error',
            detail: 'Failed to retrieve the customer list',
            boolean: true
        }
        this.messageService.add(message);
        this.loading = false;
      });
  }

  private applyParamsOnRoute() {
    const queryParams = { ...this.filter };

    this.reloadRouteWithParams(queryParams);
  }

  private reloadRouteWithParams(queryParams: any) {
    this.router.navigate(
      [],
      {
        relativeTo: this.activatedRoute,
        queryParams: queryParams,
      }
    );
  }

  public doSearch(filter: CustomerFilter) {
    this.filter = filter;
    this.applyParamsOnRoute();
  }

}
