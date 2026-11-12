import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-category-edit-page',
  templateUrl: './category-edit-page.component.html',
  styleUrl: './category-edit-page.component.css'
})
export class CategoryEditPageComponent implements OnInit {
  categoryId?: string;

  constructor(
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.categoryId = this.route.snapshot.params['categoryId']!;
  }
}
