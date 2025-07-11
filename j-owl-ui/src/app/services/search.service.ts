import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';

import {environment} from '../../environments/environment.development';

export interface SearchResult {
  name: string;
  url: string;
  snippet: string;
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
    return this.http.get<SearchResult[]>(this.SEARCH_API_URL, {params})
      .pipe(
        catchError(error => {
          console.error('Error occurred during search operation', error);
          throw new Error(error);
        })
  );
  }
}
