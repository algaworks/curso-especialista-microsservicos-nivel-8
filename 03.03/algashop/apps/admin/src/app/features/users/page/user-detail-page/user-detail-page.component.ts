import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem, MessageService } from 'primeng/api';

import { enumToString, sleep } from 'src/app/shared/shared.module';
import { UsersService } from '../../users.service';
import { firstValueFrom } from 'rxjs';
import { UserModel, UserType } from '../../models/model';

@Component({
  selector: 'app-user-detail-page',
  templateUrl: './user-detail-page.component.html',
  styleUrl: './user-detail-page.component.css'
})
export class UserDetailPageComponent implements OnInit {
  user?: UserModel;
  userId?: string;
  loading = true;
  menuItems: MenuItem[] = [];

  constructor(
    private usersService: UsersService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    ) { }

  ngOnInit(): void {
    this.userId = this.route.snapshot.params['userId'];
    this.loadData();
    this.setupMenus();
  }

  private loadData() {
    this.loading = true; // on page refresh
    sleep()
      .then(()=> {
        this.loadUser();
      });
  }

  private setupMenus() {
    this.menuItems = this.menuItems = [
      {
        label: 'Options',
        items: [
          {
            label: 'Edit',
            command: () => {
              this.edit();
            }
          }
        ]
      }
    ];
  }

  private edit() {
    if (this.userId) {
      this.router.navigate([`/users/${this.userId}/edit`])
    }
  }

  private loadUser() {
    firstValueFrom(this.usersService.getOne(this.userId!))
      .then(user => {
        this.user = user;
        this.loading = false;
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Server connection error',
            detail: 'Unable to retrieve user',
            boolean: true
        }
        this.messageService.add(message);
      });
  }

  public enumToValue(enumObj: UserType) {
    return enumToString(UserType, enumObj);
  }

}
