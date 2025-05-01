import { Component } from '@angular/core';
import { SearchService, SearchResult} from '../../services/search.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  imports: [
    FormsModule
  ],
  styleUrl: './search.component.css'
})
export class SearchComponent {

  query: string = "";
  results: SearchResult[] = [];
  loading: boolean = false;
  error: string | null = null;

  constructor(private searchService: SearchService) {
  }

  onSearch(): void {
    this.loading = true;
    this.error = null;
    this.results = [];

    this.searchService.search(this.query, 50).subscribe({
      next: (data) => {
        this.results = data;
        this.loading = false;
      },
      error: err => {
        this.error = "Error fetching search results.";
        console.error(err);
        this.loading = false
      }
    })
  }
}
