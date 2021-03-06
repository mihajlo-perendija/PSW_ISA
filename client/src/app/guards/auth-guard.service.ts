import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, Route } from '@angular/router';
import { Observable } from 'rxjs';

import { UserService } from '../services/user.service';


@Injectable({
	providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

	constructor(private userService: UserService,
		private router: Router) { }

	canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
		const currentUser = this.userService.checkLoggedIn();
        if (currentUser) {
            // check if route is restricted by role
            if (route.data.roles){
                currentUser.roles.forEach(role => {
                    if (route.data.roles.indexOf(role) === -1) {
                        // role not authorised so redirect to home page
                        alert('Not authorised!')
                        if (currentUser.roles.indexOf('PATIENT')!= -1){
							this.router.navigate(['/patient']);
						} else {
							this.router.navigate(['/']);
						}
                        return false;
                    }
                });
            }
            

            // authorised so return true
            return true;
        }

        // not logged in so redirect to login page with the return url
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return false;
    }
	
}
