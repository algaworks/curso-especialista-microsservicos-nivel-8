import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SalesOverview {
  yearMonth: string;
  quantity: number;
  revenue: number;
}

export interface TotalOverview {
  activeCustomers: number;
  activeShoppingCarts: number;
}

export interface PaymentsOverview {
  yearMonth: string;
  paymentMethod: string;
  quantity: number;
  totalAmount: number;
}

export interface TopSellingProduct {
  productId: string;
  productName: string;
  categoryName: string;
  quantity: number;
  revenue: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private url: string;

  constructor(private http: HttpClient) {
    this.url = `${environment.apiUrl}/api/v1`;
  }

  salesOverview(yearMonthStart?: string, yearMonthEnd?: string): Observable<SalesOverview[]> {
    return this.http.get<SalesOverview[]>(`${this.url}/metrics/sales-overview`, {
      params: this.period(yearMonthStart, yearMonthEnd)
    });
  }

  totalOverview(): Observable<TotalOverview> {
    return this.http.get<TotalOverview>(`${this.url}/metrics/total-overview`);
  }

  paymentsOverview(yearMonthStart?: string, yearMonthEnd?: string): Observable<PaymentsOverview[]> {
    return this.http.get<PaymentsOverview[]>(`${this.url}/metrics/payments-overview`, {
      params: this.period(yearMonthStart, yearMonthEnd)
    });
  }

  topSellingProducts(yearMonth?: string): Observable<TopSellingProduct[]> {
    let params = new HttpParams();
    if (yearMonth) {
      params = params.set('yearMonth', yearMonth);
    }
    return this.http.get<TopSellingProduct[]>(`${this.url}/insights/top-selling-products`, { params });
  }

  private period(yearMonthStart?: string, yearMonthEnd?: string): HttpParams {
    let params = new HttpParams();
    if (yearMonthStart) {
      params = params.set('yearMonthStart', yearMonthStart);
    }
    if (yearMonthEnd) {
      params = params.set('yearMonthEnd', yearMonthEnd);
    }
    return params;
  }
}
