import { Component, OnInit } from '@angular/core';

import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-public-home',
  templateUrl: './public-home.component.html',
  styleUrls: ['./public-home.component.css']
})
export class PublicHomeComponent implements OnInit {
  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    if (this.authService.hasValidToken()) {
      this.authService.login('/dashboard');
    }
  }

  login() {
    this.authService.login('/dashboard');
  }
}
