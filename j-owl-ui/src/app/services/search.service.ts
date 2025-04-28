import { Injectable } from '@angular/core';
import { HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface SearchResult {
  name: string;
  url: string;
  snippet: string;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private readonly API_URL = "http://localhost:8080/index/search"

  constructor(private http: HttpClient) { }

  search(query: string, maxResults: number = 10): Observable<SearchResult[]> {
    return this.http.get<SearchResult[]>(`${this.API_URL}?query=${query}&maxResults=${maxResults}`);
  }
}
