import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { Subscription } from 'rxjs';
import { Mustertext, MUSTRETEXT_KATEGORIE } from '../../shared/shared-entities.model';
import { MustertexteFacade } from '../mustertexte.facade';
import { mustertextKategoriValidator } from './edit-mustertext.validators';

@Component({
  selector: 'mka-edit-mustertext',
  templateUrl: './edit-mustertext.component.html',
  styleUrls: ['./edit-mustertext.component.css']
})
export class EditMustertextComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';

  mustertextForm!: FormGroup;

  kategorieControl!: FormControl;

  nameControl!: FormControl;

  textControl!: FormControl;

  editorInitialized = false;

  saveInProgress = false;

  kategorien: MUSTRETEXT_KATEGORIE[] = ['UNDEFINED', 'MAIL', 'NEWSLETTER'];

  private uuid: string = '';

  private editorModelSubscription: Subscription = new Subscription();

  constructor(private fb: FormBuilder,
    private router: Router,
    public mustertexteFacade: MustertexteFacade,
    private messageService: MessageService) { 

      this.initForm()
    }

  ngOnInit(): void {

    this.editorModelSubscription = this.mustertexteFacade.editorModel$.subscribe(

      model => {

        if (model) {

          const kategorie = this.mustertextForm.get('kategorie');
          const name = this.mustertextForm.get('name');
          const text = this.mustertextForm.get('text');

          if (kategorie) {
            kategorie.setValue(model.kategorie, { onlySelf: true });
          }

          if (name) {
            name.setValue(model.name, { onlySelf: true });
          }

          if (text) {
            text.setValue(model.text, { onlySelf: true });
          }

				  this.uuid = model.uuid;
        }
       }
    );

    this.saveInProgress = false;
    this.editorInitialized = true;
  }

  ngOnDestroy(): void {
    this.editorModelSubscription.unsubscribe();      
  }

  onSubmit(): void {

    this.saveInProgress = true;
    const formValue = this.mustertextForm.value;

    const daten: Mustertext = {
      uuid: this.uuid,
      kategorie: formValue.kategorie,
      name: formValue.name.trim(),
      text: formValue.text
    };

    this.mustertexteFacade.saveMustertext(daten);
    this.saveInProgress = false;
  }

  onCancel(): void {

    this.messageService.clear();
    this.mustertexteFacade.cancelEditMustertext();
    this.router.navigateByUrl('/mustertexte');
  }


  private initForm(): void {

    this.kategorieControl = new FormControl({value: 'UNDEFINED'}, {validators: [Validators.required, mustertextKategoriValidator]})
    this.nameControl = new FormControl({value: ''}, {validators: [Validators.required, Validators.maxLength(55)]})
    this.textControl = new FormControl({value: ''}, {validators: [Validators.required]})

    this.mustertextForm = this.fb.group({
      kategorie: this.kategorieControl,
      name: this.nameControl,
      text: this.textControl
    });
  }

}
