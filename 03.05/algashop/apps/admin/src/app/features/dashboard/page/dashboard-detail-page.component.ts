import { Component, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { Observable, Subscription, debounceTime, firstValueFrom, forkJoin } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { OrdersService } from '../../orders/orders.service';
import { OrderFilter, OrderModel, OrderSortProperty } from '../../orders/models/model';
import { DashboardService, TopSellingProduct } from '../dashboard.service';

@Component({
  selector: 'app-dashboard-detail-page',
  templateUrl: './dashboard-detail-page.component.html',
  styleUrl: './dashboard-detail-page.component.css'
})
export class DashboardDetailPageComponent implements OnInit {

  readonly showDashboardExtraCards = environment.showDashboardExtraCards;

  items!: MenuItem[];

  chartData: any;

  chartOptions: any;

  subscription!: Subscription;

  orders: OrderModel[] = [];

  loadingOrders = true;

  recordsPerPage = 5;

  monthlyOrders = 0;

  monthlyRevenue = 0;

  activeCustomers = 0;

  activeShoppingCarts = 0;

  topSellingProducts: TopSellingProduct[] = [];

  selectedMonth: Date = new Date();

  private metricsRequest = 0;

  private ordersRequest = 0;

  constructor(
    private messageService: MessageService,
    private ordersService: OrdersService,
    private dashboardService: DashboardService) {

  }

  ngOnInit(): void {
    this.initChart();
    this.loadOrders();
    if (this.showDashboardExtraCards) {
      this.loadTotals();
      this.loadMetrics();
    }
  }

  get selectedYear(): number {
    return this.selectedMonth.getFullYear();
  }

  onMonthChange() {
    if (!this.selectedMonth) {
      this.selectedMonth = new Date();
    }
    this.loadOrders();
    this.loadMetrics();
  }

  private loadTotals() {
    this.load('customers and shopping carts',
      this.dashboardService.totalOverview(),
      total => {
        this.activeCustomers = total.activeCustomers;
        this.activeShoppingCarts = total.activeShoppingCarts;
      });
  }

  private loadMetrics() {
    const requestId = ++this.metricsRequest;
    const yearMonth = this.toYearMonth(this.selectedMonth);
    const year = this.selectedYear;

    this.load('monthly orders',
      this.dashboardService.salesOverview(yearMonth, yearMonth),
      sales => this.monthlyOrders = sales.reduce((total, month) => total + month.quantity, 0),
      requestId);

    this.load('monthly revenue',
      this.dashboardService.paymentsOverview(yearMonth, yearMonth),
      payments => this.monthlyRevenue = payments.reduce((total, payment) => total + payment.totalAmount, 0),
      requestId);

    this.load('best monthly selling products',
      this.dashboardService.topSellingProducts(yearMonth),
      products => this.topSellingProducts = products,
      requestId);

    this.load('sales overview',
      this.dashboardService.salesOverview(`${year}-01`, `${year}-12`),
      sales => this.updateChart(sales.map(month => month.yearMonth), sales.map(month => month.quantity)),
      requestId);
  }

  private load<T>(description: string, request: Observable<T>, onSuccess: (result: T) => void, requestId?: number) {
    firstValueFrom(request)
      .then(result => {
        if (requestId === undefined || requestId === this.metricsRequest) {
          onSuccess(result);
        }
      })
      .catch(e => {
        this.messageService.add({
          severity: 'error',
          summary: 'Server connection error',
          detail: `Failed to retrieve the ${description}`
        });
      });
  }

  private toYearMonth(date: Date): string {
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${date.getFullYear()}-${month}`;
  }

  
  private loadOrders() {
    const requestId = ++this.ordersRequest;

    this.loadingOrders = true;
    // The orders API filters by a single status, and a paid order is either PAID or READY
    forkJoin(['PAID', 'READY'].map(status => this.ordersService.filter(this.recentSalesFilter(status))))
      .subscribe({
        next: pages => {
          if (requestId === this.ordersRequest) {
            this.orders = ([] as OrderModel[])
              .concat(...pages.map(page => page.content))
              .sort((a, b) => new Date(b.placedAt).getTime() - new Date(a.placedAt).getTime())
              .slice(0, this.recordsPerPage);
            this.loadingOrders = false;
          }
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Server connection error',
            detail: 'Failed to retrieve the order list'
          });
          this.loadingOrders = false;
        }
      });
  }

  private recentSalesFilter(status: string): OrderFilter {
    const filter = new OrderFilter();
    filter.size = this.recordsPerPage;
    filter.sort = `${OrderSortProperty.PLACE_AT},DESC`;
    filter.status = status;

    if (this.showDashboardExtraCards) {
      const year = this.selectedMonth.getFullYear();
      const month = this.selectedMonth.getMonth();
      filter.placedAtFrom = new Date(year, month, 1);
      filter.placedAtTo = new Date(year, month + 1, 0);
    }
    return filter;
  }

  initChart() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.chartData = {
        labels: [],
        datasets: [
            {
                label: 'Sales per month',
                data: [],
                fill: false,
                backgroundColor: documentStyle.getPropertyValue('--bluegray-700'),
                borderColor: documentStyle.getPropertyValue('--bluegray-700'),
                tension: .4
            }
        ]
    };

    this.chartOptions = {
        plugins: {
            legend: {
                labels: {
                    color: textColor
                }
            }
        },
        scales: {
            x: {
                ticks: {
                    color: textColorSecondary
                },
                grid: {
                    color: surfaceBorder,
                    drawBorder: false
                }
            },
            y: {
                ticks: {
                    color: textColorSecondary
                },
                grid: {
                    color: surfaceBorder,
                    drawBorder: false
                }
            }
        }
    };
  }

  private updateChart(yearMonths: string[], quantities: number[]) {
    const labels = yearMonths.map(yearMonth => {
      const [year, month] = yearMonth.split('-').map(Number);
      return new Date(year, month - 1, 1).toLocaleString('en', { month: 'long' });
    });

    this.chartData = {
      labels,
      datasets: [{ ...this.chartData.datasets[0], data: quantities }]
    };
  }

}
