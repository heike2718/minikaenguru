import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { emailValidator } from '@minikaenguru-ws/common-components';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { Subscription } from 'rxjs';
import { MustertexteFacade } from '../mustertexte.facade';
import { Mail } from '../mustertexte.model';

@Component({
  selector: 'mka-send-mail',
  templateUrl: './send-mail.component.html',
  styleUrls: ['./send-mail.component.css']
})
export class SendMailComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';

  mailForm!: FormGroup;

  emailControl!: FormControl;

  betreffControl!: FormControl;

  textControl!: FormControl;

  editorInitialized = false;

  saveInProgress = false;

  private mailSubscription: Subscription = new Subscription();

  constructor(private fb: FormBuilder,
    private router: Router,
    public mustertexteFacade: MustertexteFacade,
    private messageService: MessageService) { 

      this.initForm();

    }

  ngOnInit(): void {

    this.mailSubscription = this.mustertexteFacade.mail$.subscribe (

      mail => {

        if (mail) {

          const email = this.mailForm.get('email');
          const betreff = this.mailForm.get('betreff');
          const text = this.mailForm.get('text');

          if (email) {
            email.setValue('', {onlySelf: true});
          }

          if (betreff) {
            betreff.setValue(mail.betreff, {onlySelf: true});
          }

          if (text) {
            text.setValue(mail.mailtext, { onlySelf: true });
          }

        }
      }
    );

    this.saveInProgress = false;
		this.editorInitialized = true;

  }

  ngOnDestroy(): void {
      this.mailSubscription.unsubscribe();
  }

  onSubmit(): void {

    this.saveInProgress = true;
    const formValue = this.mailForm.value;

    const daten: Mail = {
      empfaenger: formValue.email.trim(),
      betreff: formValue.betreff.trim(),
      mailtext: formValue.text
    };

    this.mustertexteFacade.sendMail(daten);
    this.saveInProgress = false;
  }

  onCancel(): void {

    this.messageService.clear();
    this.mustertexteFacade.clearMail();
    this.router.navigateByUrl('/mustertexte');
  }


  private initForm(): void {

    this.emailControl = new FormControl({value: ''}, {validators: [Validators.required, emailValidator]})
    this.betreffControl = new FormControl({value: ''}, {validators: [Validators.required, Validators.minLength(5)]})
    this.textControl = new FormControl({value: ''}, {validators: [Validators.required, Validators.minLength(5)]})

    this.mailForm = this.fb.group({
      email: this.emailControl,
      betreff: this.betreffControl,
      text: this.textControl
    });
  }
}
