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

  copyToClipboard(result: SearchResult): void {
    let textToCopy: string;
    
    // For method results, include more context
    if (result.methodSignature || result.className) {
      const parts: string[] = [];
      
      if (result.packageName) {
        parts.push(`Package: ${result.packageName}`);
      }
      if (result.className) {
        parts.push(`Class: ${result.className}`);
      }
      if (result.methodSignature) {
        parts.push(`Method: ${result.methodSignature}`);
      }
      if (result.returnType) {
        parts.push(`Returns: ${result.returnType}`);
      }
      if (result.modifiers) {
        parts.push(`Modifiers: ${result.modifiers}`);
      }
      
      textToCopy = parts.join('\n');
    } else {
      // For class results, just use the name
      textToCopy = result.name;
    }
    
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(textToCopy).then(() => {
        this.showCopyFeedback();
      }).catch(err => {
        console.error('Failed to copy: ', err);
        this.fallbackCopyToClipboard(textToCopy);
      });
    } else {
      this.fallbackCopyToClipboard(textToCopy);
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
    const allResults = this.results.map(result => {
      if (result.methodSignature || result.className) {
        const parts: string[] = [];
        
        if (result.packageName) {
          parts.push(`Package: ${result.packageName}`);
        }
        if (result.className) {
          parts.push(`Class: ${result.className}`);
        }
        if (result.methodSignature) {
          parts.push(`Method: ${result.methodSignature}`);
        }
        if (result.returnType) {
          parts.push(`Returns: ${result.returnType}`);
        }
        
        return parts.join(' | ');
      } else {
        return result.name;
      }
    }).join('\n');
    
    this.fallbackCopyToClipboard(allResults);
  }

  isMethodResult(result: SearchResult): boolean {
    return !!(result.methodSignature || result.returnType || result.modifiers);
  }

  getResultTypeLabel(result: SearchResult): string {
    return this.isMethodResult(result) ? 'Method' : 'Class';
  }
}
