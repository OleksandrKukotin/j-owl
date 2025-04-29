import { Component } from '@angular/core';
import { SearchService, SearchResult} from '../../services/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent {

  query: string = "";
  results: SearchResult[] = [];
  loading: boolean = false;
  error: string | null = null;

  constructor(private searchService: SearchService) {
  }
}
