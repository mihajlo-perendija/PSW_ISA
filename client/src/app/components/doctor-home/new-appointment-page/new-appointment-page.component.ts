import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ClinicService } from 'src/app/services/clinic.service';
import { UserService } from 'src/app/services/user.service';
import { ActivatedRoute } from '@angular/router';
import { PatientService } from 'src/app/services/patient.service';

@Component({
	selector: 'app-new-appointment-page',
	templateUrl: './new-appointment-page.component.html',
	styleUrls: ['./new-appointment-page.component.css']
})
export class NewAppointmentPageComponent implements OnInit {
	doctor: any;
	patient: any;
	typesOfExamination: any;
	ordinations: any;

	form: FormGroup;
	submitted: boolean;
	private sub: any;
	constructor(private formBuilder: FormBuilder,
		private userService: UserService,
		private clinicService: ClinicService,
		private patientService: PatientService,
		private activatedRoute: ActivatedRoute) { }

	ngOnInit() {
		this.sub = this.activatedRoute.params.subscribe(params => {
			let patient_id = +params['patient_id'];
			
			this.loadData(patient_id).then(() => {
				this.createFormGroup();
			}, () => alert("Error loading data"))
		});
	}

	loadData(patient_id){
		let promise = new Promise((resolve, reject) => {
			this.loadPatientInfo(patient_id).then(() => {
				this.loadTypesOfExamination().then(() => {
					this.loadOrdinations().then(() => {
						resolve();
					}, () => reject());
				}, () => reject());
			}, () => reject());
			
		});
		return promise;
	}

	loadPatientInfo(patient_id) {
		let promise = new Promise((resolve, reject) => {
			this.patientService.getPatientById(patient_id).subscribe(
				(data) => { this.patient = data; resolve(); },
				(error) => { alert(error); reject(); }
			);
		});
		return promise;
	}

	loadTypesOfExamination() {
		let promise = new Promise((resolve, reject) => {
			let clinic_id = this.userService.getUser().clinic_id;
			this.clinicService.getTypesOfExamination(clinic_id).subscribe(
				(data) => { this.typesOfExamination = data; resolve(); },
				(error) => { alert(error); reject(); }
			);
		});
		return promise;
	}

	loadOrdinations() {
		let promise = new Promise((resolve, reject) => {
			let clinic_id = this.userService.getUser().clinic_id;
			this.clinicService.getOrdinations(clinic_id).subscribe(
				(data) => { this.ordinations = data; resolve(); },
				(error) => { alert(error); reject(); }
			);
		});
		return promise;
	}

	createFormGroup() {

		this.form = this.formBuilder.group({
			dateAndTime: [, [Validators.required,]],
			duration: [, [Validators.required,]],
			typeOfExamination: [, [Validators.required,]],
			ordination: [, [Validators.required,]],
			price: [, [Validators.required,]],
		});

	}

	setPriceFromTypeOfExamination(){
		let type = this.typesOfExamination.find( element => element.id == this.form.controls.typeOfExamination.value);
		if (!type) {
			this.form.controls['price'].setValue("");
			return;
		}
		this.form.controls['price'].setValue(type.price);
	}

	onSubmit() {
		this.submitted = true;

		if (this.form.invalid) {
			return;
		}

		let doctor = this.userService.getUser();
		if (!doctor){
			alert("Invalid user");
			return;
		}
		let clinic_id = doctor.clinic_id;

		let appointment = {
			startingDateAndTime: this.form.controls.dateAndTime.value.getTime(),
			duration: this.form.controls.duration.value,
			typeOfExamination: { id: this.form.controls.typeOfExamination.value },
			ordination: { id: this.form.controls.ordination.value },
			price: this.form.controls.price.value,
			clinic: { id: clinic_id },
			patient: { id: this.patient.id }
		}

		console.log(appointment);

		// this.doctorService.changeDoctor(doctor).subscribe(
		//   (data) => { this.change = !this.change; return; },
		//   (error) => { alert(error); }
		// );

	}

	ngOnDestroy() {
		this.sub.unsubscribe();
	}
}
