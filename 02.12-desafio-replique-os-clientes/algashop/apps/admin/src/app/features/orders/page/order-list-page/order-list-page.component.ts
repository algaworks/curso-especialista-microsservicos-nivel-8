import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { OrdersService } from '../../orders.service';
import { ActivatedRoute, Router } from '@angular/router';
import { enumToString, sleep } from 'src/app/shared/shared.module';
import { TableLazyLoadEvent } from 'primeng/table';
import { OrderFilter, OrderModel, OrderSortProperty, OrderStatus } from '../../models/model';

@Component({
  selector: 'app-order-list-page',
  templateUrl: './order-list-page.component.html',
  styleUrl: './order-list-page.component.css'
})
export class OrderListPageComponent implements OnInit {
  readonly sortProperties = OrderSortProperty;
  orders: OrderModel[] = [];
  filter: OrderFilter = new OrderFilter();
  totalElements = 0;
  recordsPerPage = 0;
  loading: boolean = true;

  constructor(
    private ordersService: OrdersService,
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
        this.loadOrders();
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

  private loadOrders() {
    firstValueFrom(this.ordersService.filter(this.filter))
      .then(page => {
        this.orders = page.content;
        this.loading = false;
        this.recordsPerPage = page.size;
        this.totalElements = page.totalElements;
      })
      .catch(e => {
        let message = {
            severity:'error',
            summary: 'Server connection error',
            detail: 'Failed to retrieve the order list',
            boolean: true
        }
        this.messageService.add(message);
        this.loading = false;
      });
  }

  private applyParamsOnRoute() {
    const queryParams: any = { ...this.filter };

    if(this.filter.status && this.filter.status !== '') {
      queryParams.status = this.filter.status.value || this.filter.status;
    }
    if(this.filter.placedAtFrom) {
      queryParams.placedAtFrom = new Date(this.filter.placedAtFrom).toISOString().substring(0, 10);
    }
    if(this.filter.placedAtTo) {
      queryParams.placedAtTo = new Date(this.filter.placedAtTo).toISOString().substring(0, 10);
    }

    this.reloadRouteWithParams(queryParams);
  }

  private reloadRouteWithParams(queryParams: any) {
    this.router.navigate(
      [],
      {
        relativeTo: this.activatedRoute,
        queryParams: queryParams,
        // queryParamsHandling: 'merge',
      }
    );
  }

  public doSearch(filter: OrderFilter) {
    this.filter = filter;
    this.applyParamsOnRoute();
  }

  public enumToValue(enumObj: OrderStatus) {
    return enumToString(OrderStatus, enumObj);
  }

}
