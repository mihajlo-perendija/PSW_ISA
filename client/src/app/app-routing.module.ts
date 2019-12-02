import { DoctorListingComponent } from './components/adminC-dashboard/doctor-listing/doctor-listing.component';
import { ClinicAdminFormComponent } from './components/adminCC-dashboard/clinic-admin-form/clinic-admin-form.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AuthGuardService } from './guards/auth-guard.service'
import { AdminProfileComponent } from './components/adminCC-dashboard/admin-profile.component';
import { LoginComponent } from './components/login/login.component';
import { ClinicListingComponent } from './components/adminCC-dashboard/clinic-listing/clinic-listing.component';
import { ClinicAdministratorsListingComponent } from './components/adminCC-dashboard/clinic-administrators-listing/clinic-administrators-listing.component';
import { ClinicFormComponent } from './components/adminCC-dashboard/clinic-form/clinic-form.component';
import { MedicationListingComponent } from './components/adminCC-dashboard/medication-listing/medication-listing.component';
import { MedicationFormComponent } from './components/adminCC-dashboard/medication-form/medication-form.component';
import { DoctorFormComponent } from './components/adminC-dashboard/doctor-form/doctor-form.component';
import { AdminPersonalProfileComponent} from './components/adminCC-dashboard/admin-personal-profile/admin-personal-profile.component';
import { ClinicProfileInfoComponent } from './components/adminC-dashboard/clinic-profile-info/clinic-profile-info.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { ChangePasswordDeactivateService } from './guards/change-password-deactivate.service';
import { AdminCPersonalProfileComponent } from './components/adminC-dashboard/adminC-personal-profile/adminC-personal-profile.component';
import { AdminCDashboardComponent } from './components/adminC-dashboard/adminC-dashboard.component';
import { DiagnosisListingComponent } from './components/adminCC-dashboard/diagnosis-listing/diagnosis-listing.component';
import { DiagnosisFormComponent } from './components/adminCC-dashboard/diagnosis-form/diagnosis-form.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { OrdinationListingComponent } from './components/adminC-dashboard/ordination-listing/ordination-listing.component';
import { AvailableAppointmentListingComponent } from './components/adminC-dashboard/available-appointment-listing/available-appointment-listing.component';
import { RegistrationListingComponent } from './components/adminCC-dashboard/registration-listing/registration-listing.component';
import { TypeOfExaminationListingComponent } from './components/adminC-dashboard/type-of-examination-listing/type-of-examination-listing.component';
import { TypeOfExaminationFormComponent } from './components/adminC-dashboard/type-of-examination-form/type-of-examination-form.component';
import { OrdinationFormComponent } from './components/adminC-dashboard/ordination-form/ordination-form.component';
import { PatientHomeComponent } from './components/patient-home/patient-home/patient-home.component';
import { PatientPersonalProfileComponent } from './components/patient-home/patient-personal-profile/patient-personal-profile.component';
import { PatientClinicListingComponent } from './components/patient-home/patient-clinic-listing/patient-clinic-listing.component';

const routes: Routes = [
	{
		path: 'adminCCdashboard',
		component: AdminProfileComponent,
		children:[
			{path: '', component: AdminPersonalProfileComponent},
			{path: 'profile', component: AdminPersonalProfileComponent},
			{path: 'clinics', component: ClinicListingComponent},
			{path: 'clinicAdmins', component: ClinicAdministratorsListingComponent},
			{path: 'addClinic', component: ClinicFormComponent},
			{path: 'medications', component: MedicationListingComponent},
			{path: 'medication_info', component: MedicationFormComponent},
			{path: 'diagnoses', component: DiagnosisListingComponent},
			{path: 'diagnosis_info', component: DiagnosisFormComponent},
			{path: 'addClinicAdmin', component: ClinicAdminFormComponent},
			{path: 'showClinicInfo', component: ClinicProfileInfoComponent},
			{path: 'registrationRequests', component: RegistrationListingComponent}
		],
		canActivate: [AuthGuardService],
		data: { roles: ['ADMINCC']}
	},
	{
		path: 'adminCdashboard',
		component: AdminCDashboardComponent,
		children:[
			{path: '', component: AdminCPersonalProfileComponent},
			{path: 'profile', component: AdminCPersonalProfileComponent},
			{path: 'clinic', component: ClinicProfileInfoComponent},
			{path: 'doctors', component: DoctorListingComponent},
			{path: 'doctor', component: DoctorFormComponent},
			{path: 'available_appointments', component: AvailableAppointmentListingComponent},
			{path: 'types_of_examination', component: TypeOfExaminationListingComponent},
			{path: 'type_of_examination_info', component: TypeOfExaminationFormComponent},
			{path: 'ordinations', component: OrdinationListingComponent},
			{path: 'ordination_form', component: OrdinationFormComponent},

		],
		canActivate: [AuthGuardService],
		data: { roles: ['ADMINC']}
	},
	{
		path: 'patientHome',
		component: PatientHomeComponent,
		children:[
			{path: '', component: PatientPersonalProfileComponent},
			{path: 'profile', component: PatientPersonalProfileComponent},
			{path: 'clinics', component: PatientClinicListingComponent},

		],
		canActivate: [AuthGuardService],
		data: { roles: ['PATIENT']}
	},
	// {
	// 	path: 'nurseHome',
	// 	component: PatientHomeComponent,
	// 	children:[
	// 		{path: '', component: PatientPersonalProfileComponent},
	// 		{path: 'profile', component: PatientPersonalProfileComponent},

	// 	],
	// 	canActivate: [AuthGuardService],
	// 	data: { roles: ['NURSE']}
	// },
	// {
	// 	path: 'doctorHome',
	// 	component: PatientHomeComponent,
	// 	children:[
	// 		{path: '', component: PatientPersonalProfileComponent},
	// 		{path: 'profile', component: PatientPersonalProfileComponent},

	// 	],
	// 	canActivate: [AuthGuardService],
	// 	data: { roles: ['DOCTOR']}
	// },
	{
		path: 'login',
		component: LoginComponent
	},
	{
		path: 'register',
		component: RegistrationComponent
	},
	{
		path: 'change-password',
		component: ChangePasswordComponent,
		canDeactivate: [ChangePasswordDeactivateService]
	}
	//{
	// 	path: '',
	// 	component: HomeComponent,
	// 	canActivate: [AuthGuardService]
	// },
	// {
	// 	path: 'admin',
	// 	component: AdminComponent,
	// 	canActivate: [AuthGuardService],
	// 	data: { roles: ["clinicalCenterAdmin, clinicAdmin, doctor, nurse, patient"] }
	// },
	// {
	// 	path: 'login',
	// 	component: LoginComponent
	// },
	// {
	// 	path: 'register',
	// 	component: RegisterComponent
	// },
	// // otherwise redirect to home
	// { path: '**', redirectTo: '' }
];

@NgModule({
	imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
	exports: [RouterModule]
})
export class AppRoutingModule { }
