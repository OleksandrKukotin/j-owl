import { Component } from '@angular/core';
import { SearchService, SearchResult} from '../../services/search.service';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  imports: [
    FormsModule,
    CommonModule
  ],
  styleUrls: ['./search.component.css']
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

    this.searchService.search(this.query, 50)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
      next: (data) => {
        this.results = data;
      },
      error: err => {
        this.error = "Error fetching search results.";
        console.error(err);
      }
    })
  }
}
