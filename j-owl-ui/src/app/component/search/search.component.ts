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
  hasSearched: boolean = false;

  constructor(private searchService: SearchService) {
  }

  onSearch(): void {
    if (!this.query.trim()) {
      return;
    }

    this.loading = true;
    this.error = null;
    this.results = [];
    this.hasSearched = true;

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
        this.error = "Error fetching search results. Please try again.";
        console.error('Search error:', err);
      }
    })
  }

  clearError(): void {
    this.error = null;
  }

  copyToClipboard(text: string): void {
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(text).then(() => {
        this.showCopyFeedback();
      }).catch(err => {
        console.error('Failed to copy: ', err);
        this.fallbackCopyToClipboard(text);
      });
    } else {
      this.fallbackCopyToClipboard(text);
    }
  }

  private fallbackCopyToClipboard(text: string): void {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    textArea.style.position = 'fixed';
    textArea.style.left = '-999999px';
    textArea.style.top = '-999999px';
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    
    try {
      document.execCommand('copy');
      this.showCopyFeedback();
    } catch (err) {
      console.error('Fallback copy failed: ', err);
    }
    
    document.body.removeChild(textArea);
  }

  private showCopyFeedback(): void {
    // Simple feedback - you could enhance this with a toast notification
    const button = event?.target as HTMLElement;
    if (button) {
      const originalText = button.innerHTML;
      button.innerHTML = '<i class="bi bi-check"></i>';
      button.classList.add('btn-success');
      button.classList.remove('btn-outline-secondary');
      
      setTimeout(() => {
        button.innerHTML = originalText;
        button.classList.remove('btn-success');
        button.classList.add('btn-outline-secondary');
      }, 1000);
    }
  }

  copyAllResults(): void {
    const allNames = this.results.map(result => result.name).join('\n');
    this.copyToClipboard(allNames);
  }
}
