import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenStorageService } from '../auth/token-storage.service';

export const authGuard: CanActivateFn = (route, state) => {
  const tokenStorageService = inject(TokenStorageService);
  const router = inject(Router);

  const token = tokenStorageService.getToken();

  if (!token) {
    return router.createUrlTree(['/login']); // redirige vers login
  }

  const roles = tokenStorageService.getAuthorities();

  for (let i=0; i<route.data['roles'].lenght; i++){
    for(let j=0; j<roles.length; j++){
      if(route.data['roles'][i]===roles[j]){
        return true;
      }
    }
  }

  return router.parseUrl('/');
};

