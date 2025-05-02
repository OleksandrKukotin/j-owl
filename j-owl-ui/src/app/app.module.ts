import {HttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {SearchComponent} from './component/search/search.component';
import {BrowserModule} from '@angular/platform-browser';

@NgModule({
  imports: [
    BrowserModule,
    HttpClient,
    FormsModule,
    AppComponent,
    SearchComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
