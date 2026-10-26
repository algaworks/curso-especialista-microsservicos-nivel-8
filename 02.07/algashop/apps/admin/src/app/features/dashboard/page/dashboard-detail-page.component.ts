import { Component, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { Subscription, debounceTime, firstValueFrom } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { OrdersService } from '../../orders/orders.service';
import { OrderFilter, OrderModel } from '../../orders/models/model';

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

  totalElements = 0;
  
  recordsPerPage = 5;

  constructor(private messageService: MessageService, private ordersService: OrdersService) {

  }

  ngOnInit(): void {
    this.initChart();
    this.loadOrders();
  }

  
  private loadOrders() {
    firstValueFrom(this.ordersService.filter(new OrderFilter()))
      .then(page => {
        this.orders = page.content;
        this.loadingOrders = false;
      })
      .catch(e => {
        let message = {
            severity:'error',
            summary: 'Server connection error',
            detail: 'Failed to retrieve the order list',
            boolean: true
        }
        this.messageService.add(message);
        this.loadingOrders = false;
      });
  }

  initChart() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.chartData = {
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
        datasets: [
            {
                label: 'Sales per semester',
                data: [65, 59, 80, 81, 56, 55, 40],
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

}
