import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, map, Observable} from 'rxjs';

import {environment} from '../../environments/environment';

export interface SearchResult {
  name: string;
  url: string;
  snippet: string;
  // Method-specific fields (only present for method results)
  methodSignature?: string;
  returnType?: string;
  modifiers?: string;
  className?: string;
  packageName?: string;
  shortDescription?: string;
}

export interface SearchResponse {
  query: string;
  results: SearchResult[];
  totalResults: number;
  maxResults: number;
  timestamp: string;
  durationMs: number;
  metadata?: any;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private readonly SEARCH_API_URL = environment.searchApiUrl;

  constructor(private http: HttpClient) {
  }

  search(query: string, maxResults: number = 10): Observable<SearchResult[]> {
    const params = new HttpParams()
      .set('query', query)
      .set('maxResults', maxResults.toString());
    return this.http.get<SearchResponse>(this.SEARCH_API_URL, {params})
      .pipe(
        catchError(error => {
          console.error('Error occurred during search operation', error);
          throw new Error(error);
        })
      )
      .pipe(
        // Extract results from SearchResponse
        map(response => response.results)
      );
  }
}
