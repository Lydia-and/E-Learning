import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  url = 'http://localhost:8091/enrollments';


  constructor(private httpClient: HttpClient) { }

  getDetails(){
    return this.httpClient.get(this.url+"dashboard/details");
  }

}
