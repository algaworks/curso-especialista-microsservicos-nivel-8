import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/core/auth.service';
import { MenuDataService } from 'src/app/shared/services/menu-data.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  menuIcon = 'pi-angle-double-left';
  isMenuVisible = true;
  userName? = '';
  userEmail? = '';

  constructor(
    private menuDataService: MenuDataService, 
    private authService: AuthService
  ) 
    { }

  ngOnInit(): void {
    let that = this;
        this.menuDataService.toggleMenuBar.subscribe(function (data: boolean) {
          that.isMenuVisible = data;
          that.menuIcon = that.isMenuVisible ? 'pi-angle-double-left' : 'pi-angle-double-right';
      });
    this.userName = this.authService.userName || '';
    this.userEmail = this.authService.userEmail || '';
  }

  toggleMenu() {
    this.isMenuVisible = !this.isMenuVisible;
    this.menuDataService.toggleMenuBar.next(this.isMenuVisible);
  }

  logout() {
    this.authService.logout();
  }
}
