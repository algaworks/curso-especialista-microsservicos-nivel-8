import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem, MessageService } from 'primeng/api';


import { sleep } from 'src/app/shared/shared.module';
import { CategoriesService } from '../../categories.service';
import { firstValueFrom } from 'rxjs';
import { CategoryModel } from '../../models/models';

@Component({
  selector: 'app-category-detail-page',
  templateUrl: './category-detail-page.component.html',
  styleUrl: './category-detail-page.component.css'
})
export class CategoryDetailPageComponent implements OnInit {
  category?: CategoryModel;
  categoryId?: string;
  loading = true;
  menuItems: MenuItem[] = [];

  error = true;

  constructor(
    private categoriesService: CategoriesService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    ) { }

  ngOnInit(): void {
    this.categoryId = this.route.snapshot.params['categoryId'];
    this.loadData();
    this.setupMenus();
  }

  private loadData() {
    this.loading = true; // on page refresh
    sleep()
      .then(()=> {
        this.loadCategory();
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
    if (this.categoryId) {
      this.router.navigate([`/categories/${this.categoryId}/edit`])
    }
  }

  private loadCategory() {
    firstValueFrom(this.categoriesService.getOne(this.categoryId!))
      .then(category => {
        this.category = category;
        this.loading = false;
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Connection error with the server',
            detail: 'Unable to retrieve the category',
            boolean: true
        }
        this.messageService.add(message);
      });
  }

}
