import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from '../_services/storage.services;

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
  constructor(private storage: StorageService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const user = this.storage.getUser();
    let authReq = req;

    // Si l'utilisateur est connecté, on ajoute le Header Authorization
    if (user && user.token) {
       // Ton backend attend "Bearer <token>"
      authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${user.token}` } 
      });
    }

    return next.handle(authReq);
  }
}

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
];