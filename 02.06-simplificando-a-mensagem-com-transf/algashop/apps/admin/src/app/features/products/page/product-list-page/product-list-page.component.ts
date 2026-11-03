import { Component, OnInit } from '@angular/core';
import { LazyLoadEvent, MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { Page, ProductModel, ProductFilter, ProductSortProperty } from 'src/app/core/models';
import { ProductsService } from '../../products.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from 'primeng/dynamicdialog';
import { sleep } from 'src/app/shared/shared.module';
import { TableLazyLoadEvent } from 'primeng/table';

@Component({
  selector: 'app-product-list-page',
  templateUrl: './product-list-page.component.html',
  styleUrl: './product-list-page.component.css'
})
export class ProductListPageComponent implements OnInit {
  readonly sortProperties = ProductSortProperty;
  products: ProductModel[] = [];
  filter: ProductFilter = new ProductFilter();
  totalElements = 0;
  recordsPerPage = 0;
  loading: boolean = true;
  error: boolean = false;

  constructor(
    private productService: ProductsService,
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute,
    private dialogService: DialogService,
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
        this.loadProducts();
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

  private loadProducts() {
    firstValueFrom(this.productService.filter(this.filter))
      .then(page => {
        this.products = page.content;
        this.loading = false;
        this.recordsPerPage = page.size;
        this.totalElements = page.totalElements;
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Connection error with the server',
            detail: 'Unable to retrieve the list of products',
            boolean: true
        }
        this.messageService.add(message);
        this.loading = false;
        this.error = true;
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

  public doSearch(filter: ProductFilter) {
    this.filter = filter;
    this.applyParamsOnRoute();
  }
}
