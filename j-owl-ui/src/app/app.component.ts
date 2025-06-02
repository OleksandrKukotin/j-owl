import { Component } from '@angular/core';
import {SearchComponent} from './component/search/search.component';

@Component({
  selector: 'app-root',
  imports: [SearchComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'j-owl-ui';
}
