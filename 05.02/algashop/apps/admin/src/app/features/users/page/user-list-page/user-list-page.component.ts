import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { UsersService } from '../../users.service';
import { ActivatedRoute, Router } from '@angular/router';
import { enumToString, sleep } from 'src/app/shared/shared.module';
import { TableLazyLoadEvent } from 'primeng/table';
import { UserFilter, UserModel, UserSortProperty, UserType } from '../../models/model';

@Component({
  selector: 'app-user-list-page',
  templateUrl: './user-list-page.component.html',
  styleUrl: './user-list-page.component.css'
})
export class UserListPageComponent implements OnInit {
  readonly sortProperties = UserSortProperty;
  users: UserModel[] = [];
  filter: UserFilter = new UserFilter();
  totalElements = 0;
  recordsPerPage = 0;
  loading: boolean = true;

  constructor(
    private usersService: UsersService,
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
        this.loadUsers();
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

  private loadUsers() {
    firstValueFrom(this.usersService.filter(this.filter))
      .then(page => {
        this.users = page.content;
        this.loading = false;
        this.recordsPerPage = page.size;
        this.totalElements = page.totalElements;
      })
      .catch(e => {
        let message = {
            severity:'error',
            summary: 'Server connection error',
            detail: 'Failed to retrieve the product list',
            boolean: true
        }
        this.messageService.add(message);
        this.loading = false;
      });
  }

  private applyParamsOnRoute() {
    const queryParams = { ...this.filter };

    if(this.filter.type && this.filter.type !== '') {
      queryParams.type = this.filter.type.value || this.filter.type;
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

  public doSearch(filter: UserFilter) {
    this.filter = filter;
    this.applyParamsOnRoute();
  }

  public enumToValue(enumObj: UserType) {
    return enumToString(UserType, enumObj);
  }

}
