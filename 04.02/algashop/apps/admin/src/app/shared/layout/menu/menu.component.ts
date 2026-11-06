import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuDataService } from 'src/app/shared/services/menu-data.service';

export class CustomMenuItem {
    constructor(
      public label: string,
      public icon: string,
      public routerLink: string,
      public childs: CustomMenuItem[] = [],
      public isChildVisible: boolean = false
      ) { }

}

@Component({
    selector: 'app-menu',
    templateUrl: 'menu.component.html',
    styleUrls: ['menu.component.css']
})
export class MenuComponent implements OnInit {

    items: CustomMenuItem[] = [];
    isMenuVisible: boolean = false;
    menuClass: string = 'aw-sidebar-open';
    isChangingRoute = false;

    constructor(private menuDataService: MenuDataService,
                private router: Router) { }

    ngOnInit() {
        this.items = this.createMenuList();
        let that = this;
        this.menuDataService.toggleMenuBar.subscribe(function (data: boolean) {
          that.isMenuVisible = data;
          that.menuClass = that.isMenuVisible ? 'aw-sidebar-open' : 'aw-sidebar-closed';
      });
    }

    ngOnDestroy() {
        this.menuDataService.toggleMenuBar.observers.forEach(function (element) { element.complete(); });
    }

    changeRoute(routerLink: string) {
      this.router.navigate([routerLink])
    }

    private createMenuList(): CustomMenuItem[] {
      return [
          new CustomMenuItem(
            'Dashboard',
            'pi pi-home',
            'dashboard',
            [],
            false
        ),
          new CustomMenuItem(
            'Users',
            'pi pi-users',
            'users',
            [],
            false
        ),
          new CustomMenuItem(
            'Products',
            'pi pi-desktop',
            'products',
            [],
            false
          ),
          new CustomMenuItem(
            'Orders',
            'pi pi-shopping-bag',
            'orders',
            [],
            false
          ),
          new CustomMenuItem(
            'Customers',
            'pi pi-id-card',
            'customers',
            [],
            false
          ),
          new CustomMenuItem(
            'Categories',
            'pi pi-th-large',
            'categories',
            [],
            false
          ),
          new CustomMenuItem(
            'Debug',
            'pi pi-cog',
            'debug',
            [],
            false
          ),
      ];
  }

}
