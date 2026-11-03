import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { CategoriesService } from '../../categories.service';
import { ActivatedRoute, Router } from '@angular/router';
import { sleep } from 'src/app/shared/shared.module';
import { TableLazyLoadEvent } from 'primeng/table';
import { CategoryFilter, CategoryModel, CategorySortProperty } from '../../models/models';

@Component({
  selector: 'app-category-list-page',
  templateUrl: './category-list-page.component.html',
  styleUrl: './category-list-page.component.css'
})
export class CategoryListPageComponent implements OnInit {
  readonly sortProperties = CategorySortProperty;
  categories: CategoryModel[] = [];
  filter: CategoryFilter = new CategoryFilter();
  totalElements = 0;
  recordsPerPage = 0;
  loading: boolean = true;
  error: boolean = false;

  constructor(
    private categoryService: CategoriesService,
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.filter = new CategoryFilter(params);
      this.search();
    });
  }

  search() {
    sleep()
      .then(() => {
        this.loadCategories();
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

  private loadCategories() {
    firstValueFrom(this.categoryService.filter(this.filter))
      .then(page => {
        this.categories = page.content;
        this.loading = false;
        this.recordsPerPage = page.size;
        this.totalElements = page.totalElements;
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Connection error with the server',
            detail: 'Unable to retrieve the list of categories',
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

  public doSearch(filter: CategoryFilter) {
    this.filter = filter;
    this.applyParamsOnRoute();
  }
}
