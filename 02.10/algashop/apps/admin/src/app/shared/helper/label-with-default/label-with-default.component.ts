import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-label-with-default',
  templateUrl: './label-with-default.component.html',
  styleUrls: ['./label-with-default.component.css']
})
export class LabelWithDefaultComponent implements OnInit {

  @Input() label = '';
  @Input() value? : any;
  @Input() default = 'Not informed';

  constructor() { }

  ngOnInit(): void {
  }

}
